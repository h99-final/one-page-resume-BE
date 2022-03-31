package com.f5.onepageresumebe.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.f5.onepageresumebe.domain.project.entity.ProjectImg;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@RequiredArgsConstructor
@Component
@Slf4j
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public String uploadS3Ob(MultipartFile multipartFile, String dir){

        try(InputStream inputStream = multipartFile.getInputStream()){
            String originalFileName = dir+"/"+UUID.randomUUID()+multipartFile.getOriginalFilename();

            byte[] bytes = IOUtils.toByteArray(inputStream);

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(Mimetypes.getInstance().getMimetype(originalFileName));
            objectMetadata.setContentLength(bytes.length);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, originalFileName, byteArrayInputStream, objectMetadata);
            amazonS3Client.putObject(putObjectRequest);
            byteArrayInputStream.close();

            String url = amazonS3Client.getUrl(bucket,originalFileName).toString();

            log.info("파일 업로드 성공");
            return url;

        }catch (IOException e){
            log.error("파일 업로드 실패");
            e.printStackTrace();
            return null;
        }
    }

    public void deleteProfile(String imageURL,Integer prefixLength){

        String fileName = imageURL.substring(prefixLength);

        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket,fileName));
    }

    public void deleteProjectImages(List<ProjectImg> projectImgs) {
        projectImgs.forEach(projectImg -> {
            String imageUrl = projectImg.getImageUrl();
            deleteProfile(imageUrl,53);
        });
    }


}
