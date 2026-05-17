package com.fit.microservices.produc.controller;

import com.fit.microservices.produc.dto.ImageResponse;
import com.fit.microservices.produc.service.ImageService;
import com.fit.microservices.produc.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "Image API", description = "Operations related to images")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {
    private final ImageService imageService;
    private final S3Service s3Service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary="Get list image by product ID")
    public List<ImageResponse> getImageByProductId(@PathVariable Long id) {
        return imageService.findAllByProductId(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary="Upload image to S3 and get public URL")
    public String uploadImageToS3(@RequestParam("file") MultipartFile file) {
        return s3Service.uploadFile(file);
    }
}
