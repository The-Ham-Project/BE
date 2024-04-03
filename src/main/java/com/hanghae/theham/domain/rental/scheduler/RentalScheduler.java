package com.hanghae.theham.domain.rental.scheduler;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.hanghae.theham.domain.rental.entity.Rental;
import com.hanghae.theham.domain.rental.entity.RentalImage;
import com.hanghae.theham.domain.rental.repository.RentalImageRepository;
import com.hanghae.theham.domain.rental.repository.RentalRepository;
import com.hanghae.theham.global.config.S3Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Slf4j
@Component
public class RentalScheduler {

    private final RentalRepository rentalRepository;
    private final RentalImageRepository rentalImageRepository;
    private final S3Config s3Config;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public RentalScheduler(RentalRepository rentalRepository, RentalImageRepository rentalImageRepository, S3Config s3Config) {
        this.rentalRepository = rentalRepository;
        this.rentalImageRepository = rentalImageRepository;
        this.s3Config = s3Config;
    }

    @Transactional
    @Scheduled(cron = "0 0 2 * * *") // 매일 새벽 2시에 실행
    public void cleanUpRentals() {
        List<Rental> softDeletedRentalList = rentalRepository.findSoftDeletedRentalList();
        softDeletedRentalList.forEach(this::processRentalDeletion);
        log.info("함께쓰기의 삭제된 게시글들이 완전 삭제 되었습니다.");
    }

    private void processRentalDeletion(Rental rental) {
        List<RentalImage> existingImageList = rentalImageRepository.findAllByRental(rental);
        existingImageList.forEach(this::deleteRentalImage);
        rentalRepository.hardDeleteForRental(rental.getId());
    }

    private void deleteRentalImage(RentalImage image) {
        deleteFileFromS3(image.getImageUrl());
        rentalImageRepository.hardDeleteForRentalImage(image.getRental().getId());
    }

    private void deleteFileFromS3(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String key = url.getPath().substring(1);
            s3Config.amazonS3Client().deleteObject(new DeleteObjectRequest(bucket, key));
        } catch (MalformedURLException e) {
            log.error("S3에서 파일을 삭제하는 도중 오류가 발생했습니다.", e);
            throw new RuntimeException("S3에서 파일을 삭제하는 도중 오류가 발생했습니다.", e);
        }
    }
}
