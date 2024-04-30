package com.hanghae.theham.global.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hanghae.theham.global.config.S3Config;
import com.hanghae.theham.global.exception.AwsS3Exception;
import com.hanghae.theham.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Config s3Config;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public S3Service(S3Config s3Config) {
        this.s3Config = s3Config;
    }

    public String uploadFileToS3(String directoryUrl, MultipartFile file) {
        try {
            // 파일 메타데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // 새로운 파일명 생성 (UUID 사용)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String newFilename = UUID.randomUUID() + extension;
            String key = directoryUrl + newFilename;

            // S3 업로드 요청 생성
            PutObjectRequest request = new PutObjectRequest(bucket, key, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            // S3에 파일 업로드
            s3Config.amazonS3Client().putObject(request);

            // 업로드된 파일의 URL 생성 및 반환
            return s3Config.amazonS3Client().getUrl(bucket, key).toString();
        } catch (IOException e) {
            throw new AwsS3Exception(ErrorCode.S3_UPLOAD_UNKNOWN_ERROR.getMessage());
        }
    }

    public void deleteFileFromS3(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String key = url.getPath().substring(1); // URL에서 객체 키 추출
            s3Config.amazonS3Client().deleteObject(new DeleteObjectRequest(bucket, key));
        } catch (MalformedURLException e) {
            throw new AwsS3Exception(ErrorCode.S3_DELETE_UNKNOWN_ERROR.getMessage());
        }
    }
}
