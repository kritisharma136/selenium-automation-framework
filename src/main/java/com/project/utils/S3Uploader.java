package com.project.utils;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * S3Uploader pushes test artifacts (reports, screenshots) to AWS S3
 * after each test run. On EC2, credentials come from the IAM role
 * attached to the instance — no hardcoded keys needed.
 *
 * Locally: set AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY as env vars,
 * or configure ~/.aws/credentials. If neither is set, upload is skipped
 * so local runs still work without AWS.
 */
public class S3Uploader {

    private static final Logger log = LogManager.getLogger(S3Uploader.class);

    // Read bucket name from aws.properties via ConfigReader
    private static final String BUCKET_NAME = ConfigReader.get("s3.bucket.name", "");
    private static final String REGION      = ConfigReader.get("s3.region", "ap-south-1");

    public static void upload(String localFilePath, String s3Key) {
        if (localFilePath == null || BUCKET_NAME.isEmpty()) {
            log.info("S3 upload skipped — no file path or bucket not configured");
            return;
        }

        File file = new File(localFilePath);
        if (!file.exists()) {
            log.warn("File not found for S3 upload: {}", localFilePath);
            return;
        }

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.fromName(REGION))
                    // DefaultAWSCredentialsProviderChain checks:
                    // 1. Env vars  2. ~/.aws/credentials  3. EC2 IAM role
                    .withCredentials(new DefaultAWSCredentialsProviderChain())
                    .build();

            s3Client.putObject(new PutObjectRequest(BUCKET_NAME, s3Key, file));
            log.info("Uploaded to S3: s3://{}/{}", BUCKET_NAME, s3Key);

        } catch (Exception e) {
            // Log and continue — never fail a test because of S3 issues
            log.warn("S3 upload failed (non-critical): {}", e.getMessage());
        }
    }
}
