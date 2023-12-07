package com.samedtek.conferenceboard.service.impl;

import com.samedtek.conferenceboard.entitiy.Auditorium;
import com.samedtek.conferenceboard.entitiy.Presentation;
import com.samedtek.conferenceboard.mapper.PresentationMapper;
import com.samedtek.conferenceboard.model.payload.ConferencePayload;
import com.samedtek.conferenceboard.repository.AuditoriumRepository;
import com.samedtek.conferenceboard.repository.PresentationRepository;
import com.samedtek.conferenceboard.service.ConferenceBoardService;
import com.samedtek.conferenceboard.utils.Constants;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ConferenceBoardServiceImpl implements ConferenceBoardService {

    private PresentationMapper presentationMapper;
    private PresentationRepository presentationRepository;
    private AuditoriumRepository auditoriumRepository;

    /**
     * Payload ile alınan sunumları planlar.
     *
     * @param conferencePayload
     */
    @Override
    public void scheduleConferenceBoard(ConferencePayload conferencePayload) {
        // Request ile verilen sunumları kullanacağımız formatta bir listeye mapler.
        List<Presentation> presentations = presentationMapper.mapConferencePayloadToPresentationList(conferencePayload);

        // Konferans salonu sayısını tutar
        int auditoriumNumber = getAuditoriumNumber();
        // Elimizde toplam kaç dakikalık sunum olduğunu turar
        int totalDuration = presentations.stream().mapToInt(Presentation::getDuration).sum();

        if (totalDuration != 0) {
            // Sunumlari yerleştirmek için döngü kurulur,
            // bulunan ve yerleştirilen sunumların süreleri toplam süreden çıkartılır,
            // elimizde yerleştirilmeyen sunum kalmyana kadar devam eder
            while (totalDuration != 0) {
                // Konferans salonu oluşturulur
                Auditorium auditorium = new Auditorium();
                auditorium.setName(Constants.TRACK + auditoriumNumber);

                // Öğleden önceki sunumlar bulunur ve kaydedilir
                setUpPresentationsByDuration(presentations, auditorium, Constants.BEFORE_LAUNCH);
                // Öğleden sonraki sunumlar bulunur ve kaydedilir
                setUpPresentationsByDuration(presentations, auditorium, Constants.AFTER_LAUNCH);

                // Yukarıda bulunup kaydedilen sunumlar "presentations" listesinden çıkarılmıştır.
                // Bununla birlikte tekrar toplam sunum süresi aşağıda hseaplanır
                // ve kalan sunumlar yeni bir konferans salonuna kaydedilecek şekilde döngüye devam ediler.
                if (presentations.isEmpty()) {
                    totalDuration = 0;
                } else {
                    totalDuration = presentations.stream().mapToInt(Presentation::getDuration).sum();
                    auditoriumNumber++;
                }
            }
        }
    }

    /**
     * Planlanıp kaydedilen sunumları istenen formatta döner.
     *
     * @return String
     */
    @Override
    public String getConfereneBoard() {
        List<Auditorium> auditoriums = auditoriumRepository.findAll();

        StringBuilder conferenceBoard = new StringBuilder();
        for (Auditorium auditorium : auditoriums) {
            conferenceBoard.append(auditorium.getName())
                    .append(Constants.NEW_LINE);
            for (Presentation presentation : auditorium.getPresentations()) {
                conferenceBoard.append(presentation.getStartTime())
                        .append(Constants.HYPHEN)
                        .append(presentation.getEndTime())
                        .append(Constants.SPACE)
                        .append(presentation.getTitle());
                if (Objects.nonNull(presentation.getDuration())) {
                    conferenceBoard.append(" ");
                    if (presentation.getDuration() == Constants.LIGHTNING_DURATION) {
                        conferenceBoard.append(Constants.LIGHTNING);
                    } else {
                        conferenceBoard.append(presentation.getDuration())
                                .append(Constants.MIN);
                    }
                }
                conferenceBoard.append(Constants.NEW_LINE);
            }
            conferenceBoard.append(Constants.NEW_LINE);
        }
        return conferenceBoard.toString();
    }

    @Override
    public void resetConferenceBoard() {
        List<Auditorium> auditoriums = auditoriumRepository.findAll();
        for(Auditorium auditorium : auditoriums) {
            presentationRepository.deleteAll(auditorium.getPresentations());
        }
        auditoriumRepository.deleteAll(auditoriums);
    }

    /**
     * @return int
     */
    private int getAuditoriumNumber() {
        int auditoriumNumber = auditoriumRepository.countAllByIdIsNotNull();
        return ++auditoriumNumber;
    }

    /**
     * Verilen duration değişkenine göre öğleden önceki veya öğleden sonraki sunumları bulup kayder.
     *
     * @param presentations
     * @param auditorium
     * @param duration
     */
    private void setUpPresentationsByDuration(List<Presentation> presentations, Auditorium auditorium, int duration) {
        List<Presentation> presentationsToBeScheduled = new ArrayList<>();
        if(!presentations.isEmpty()) {
            if (presentations.size() == 1) {
                schedulePresentations(duration, presentations, auditorium);
                presentations.remove(presentations.get(0));
            } else {
                findClosestSumFromPresentations(presentations, duration, presentationsToBeScheduled);
                if (!presentationsToBeScheduled.isEmpty()) {
                    presentations.removeAll(presentationsToBeScheduled);
                    schedulePresentations(duration, presentationsToBeScheduled, auditorium);
                }
            }
        }

    }

    /**
     * Sunumların başlangıç ve bitiş sürelerini ayarlayar, konferans salonunu doldurur,
     * duruma göre öğle arası ve iletişim etkinliği ekler ve kaydeder
     *
     * @param duration
     * @param presentationsToBeScheduled
     * @param auditorium
     */
    private void schedulePresentations(int duration, List<Presentation> presentationsToBeScheduled, Auditorium auditorium) {
        String startTime = "";
        if (duration == Constants.BEFORE_LAUNCH) {
            startTime = Constants.BEFORE_LAUNCH_START;
        } else if (duration == Constants.AFTER_LAUNCH) {
            startTime = Constants.AFTER_LAUNCH_START;
        }
        try {
            for (Presentation presentation : presentationsToBeScheduled) {
                presentation.setAuditorium(auditorium);
                presentation.setStartTime(startTime);
                presentation.setEndTime(calculateEndTime(startTime, presentation.getDuration()));
                presentationRepository.save(presentation);
                startTime = presentation.getEndTime();
            }
            addLaunch(startTime, duration, auditorium);
            addNetworkEvent(startTime, duration, auditorium);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gerekli kontrolleri yaparak konferans programına öğle arasını ekler.
     * Buradaki kontrol eğer 12:00 ye kadar ders varsa öğle arası eklenecek şekilde tasarlandı.
     * Tüm caseler belirtilmediği için tasarımı bu şekilde yaptım istenen değişiklikler yapılabilir.
     *
     * @param startTime
     * @param duration
     * @param auditorium
     * @throws ParseException
     */
    private void addLaunch(String startTime, int duration, Auditorium auditorium) throws ParseException {
        if (duration == Constants.BEFORE_LAUNCH
                && startTime.equals(Constants.LAUNCH_TIME)) {
            Presentation launch = new Presentation();
            launch.setTitle(Constants.LAUNCH_TITLE);
            launch.setDuration(Constants.LAUNCH_DURATION);
            launch.setStartTime(Constants.LAUNCH_TIME);
            launch.setEndTime(Constants.AFTER_LAUNCH_START);
            launch.setAuditorium(auditorium);
            presentationRepository.save(launch);
        }
    }

    /**
     * Gerekli kontrolleri yaparak konferans programına iletişim etkiliğini ekler.
     * Buradaki kontrol
     * Eğer sunumlar saat 16:00 ile 17:00 arasında biterse iletişim etkinliği son sunumun bitiş saati ile 17:00 ye kadar eklenir.
     * Eğer sunumlar 16:00 dan önce biterse iletişim etkinliği 16:00 ile 17:00 arasın eklenir.
     * Eğer sunumlar 17:00 de biterse iletişim etkinliği eklenmez
     * Eğer öğleden sonra ders yoksa iletişim etkinliği eklenmez
     * Tüm caseler belirtilmediği için tasarımı bu şekilde yaptım istenen değişiklikler yapılabilir.
     *
     * @param startTime
     * @param duration
     * @param auditorium
     * @throws ParseException
     */
    private void addNetworkEvent(String startTime, int duration, Auditorium auditorium) throws ParseException {
        if (duration == Constants.AFTER_LAUNCH
                && Constants.FORMAT.parse(startTime).after(Constants.FORMAT.parse(Constants.AFTER_LAUNCH_START))
                && Constants.FORMAT.parse(startTime).before(Constants.FORMAT.parse(Constants.END_TIME))) {
            Presentation networkEvent = new Presentation();
            networkEvent.setTitle(Constants.NETWORK_EVENT_TITLE);
            if (Constants.FORMAT.parse(startTime).before(Constants.FORMAT.parse(Constants.NETWORK_START_TIME))) {
                networkEvent.setStartTime(Constants.NETWORK_START_TIME);
            } else {
                networkEvent.setStartTime(startTime);
            }
            networkEvent.setEndTime(Constants.END_TIME);
            networkEvent.setAuditorium(auditorium);
            presentationRepository.save(networkEvent);
        }
    }

    /**
     * Sunumun başlangıç saatine, sunumun süresini ekleyerek bitiş saatini hesaplar.
     * Hesaplanan bu bitiş saati bir sonraki sunumun başlangıç saatidir.
     *
     * @param startTime
     * @param duration
     * @return String
     * @throws ParseException
     */
    private String calculateEndTime(String startTime, Integer duration) throws ParseException {
        Date startDate = Constants.FORMAT.parse(startTime);
        Date endDate = DateUtils.addMinutes(startDate, duration);
        return Constants.FORMAT.format(endDate);
    }

    /**
     * Bir listenin elemanlarının toplamı istenen değere eşit yada en yakin olan alt listesiyi bulur.
     * Bu fonksiyonda target değerine 180 dakika vererek öğleden önceye yerleşecek sunumları buldum,
     * ardından target değerıne 240 dakika vererek öğleden sonraya yerleşecek sunumları buldum.
     *
     * @param presentations
     * @param target
     * @param presentationsToBeScheduled
     */
    private static void findClosestSumFromPresentations(List<Presentation> presentations, int target,
                                                        List<Presentation> presentationsToBeScheduled) {
        int start = 0;
        int end = 0;
        //Listenin ilk elemanın süresi toplam değere atanarak başlanır
        int sum = presentations.get(0).getDuration();

        //Verilen hedef değer ile toplam süre arasındaki fark hesaplanır
        //Bu farkın 0 olması yada minumum olması hedeflenir
        int result = Math.abs(sum - target);

        // Tüm dizide gezilir
        while (end < presentations.size() - 1) {
            // Eğer toplam süre, hedeften küçükse bir sonraki elemana geçilir ve toplama eklenir
            if (sum < target) {
                end++;
                sum += presentations.get(end).getDuration();
            }
            // Eğer toplam süreö hedeften büyükse bir önceki elemana geçilir ve toplamdan çıkarılır
            else {
                sum -= presentations.get(start).getDuration();
                start++;
            }
            // Toplam süre ile hedef arasındaki fark güncellenir, döngüye devam edilir
            if (Math.abs(sum - target) < result) {
                result = Math.abs(sum - target);
            }
        }

        //Hedef değere en yakın sonuç bulunduktan sonra benzer döngü ile tekrar gezilerek
        //şartı sağlayan elemanlar listeye eklenir.
        start = 0;
        end = 0;
        sum = presentations.get(0).getDuration();
        while (end < presentations.size() - 1) {
            if (sum < target) {
                end++;
                sum += presentations.get(end).getDuration();
            } else {
                sum -= presentations.get(start).getDuration();
                start++;
            }
            if (Math.abs(sum - target) == result) {
                for (int i = start; i <= end; i++) {
                    presentationsToBeScheduled.add(presentations.get(i));
                }
                break;
            }
        }
    }

//    /**
//     * Subset Sum Problem Algoritmasıdır. Recersif yaklaşım ile çözülmüştür.
//     * Bir listenin elemanları toplamı istenen değere eşit olan alt listesiyi bulur.
//     * Bu algoritma ile sadece toplam değerleri istenen değere eşit bir alt liste varsa sonuç döndüğü için
//     * çözme gidemedim. Bunun yerine bir üstekki findClosestSumFromPresentations fonksiyonunu yazarak devam ettim.
//     *
//     * @param set
//     * @param sum
//     * @param result
//     * @return boolean
//     */
//    private boolean findSumFromList(List<Presentation> set, Integer sum, List<Presentation> result) {
//        if (sum == 0) {
//            return true;
//        }
//        if (sum < 0)
//            return false;
//        if (set.size() == 0 && sum != 0)
//            return false;
//        List<Presentation> newSet = new ArrayList<>(set);
//        newSet.remove(0);
//        result.add(set.get(0));
//        if (findSumFromList(newSet, sum - set.get(0).getDuration(), result)) {
//            return true;
//        }
//        result.remove(result.size() - 1);
//        if (findSumFromList(newSet, sum, result)) {
//            return true;
//        }
//        return false;
//    }


    @Autowired
    public void setConferenceRepository(PresentationRepository presentationRepository) {
        this.presentationRepository = presentationRepository;
    }

    @Autowired
    public void setAuditoriumRepository(AuditoriumRepository auditoriumRepository) {
        this.auditoriumRepository = auditoriumRepository;
    }

    @Autowired
    public void setConferenceMapper(PresentationMapper presentationMapper) {
        this.presentationMapper = presentationMapper;
    }
}
