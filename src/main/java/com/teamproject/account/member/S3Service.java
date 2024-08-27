package com.teamproject.account.member;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.DeleteObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${spring.cloud.aws.s3.bucket}") //aplication.properties 에적어둔 버킷정보들을 여기서쓰겠다
    private String bucket;
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    String createPresignedUrl(String path) {
        var putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket) //위에 문장으로인해 버킷명을따로안적어도 됨
                .key(path)
                .build();
        var preSignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .putObjectRequest(putObjectRequest)
                .build();
        return s3Presigner.presignPutObject(preSignRequest).url().toString();
    }
    //s3파일삭제
    public String createDeletePresignedUrl(String path) {
        var deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket) // 버킷명을 따로 지정할 필요가 없음
                .key(path)
                .build();
        var preSignRequest = DeleteObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3)) // 사전 서명된 URL의 유효 기간
                .deleteObjectRequest(deleteObjectRequest)
                .build();
        return s3Presigner.presignDeleteObject(preSignRequest).url().toString();
    }

    @Value("${spring.cloud.aws.region.static}")
    private String s3region;
    public String getS3FileUrl(String fileName) {
        String bucketName = bucket; // S3 버킷 이름
        String region = s3region; // S3 버킷의 리전
        System.out.println("리전: "+s3region);
        // S3 버킷의 파일 URL 생성
        return String.format("https://%s.s3.%s.amazonaws.com/test/%s", bucketName, region, fileName);
    }
}