package com.CareNet.CareNet.controller;

import com.CareNet.CareNet.model.Document;
import com.CareNet.CareNet.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:5173")
public class FileController {

    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        Document document = documentRepository.findById(fileId).orElse(null);
        
        if (document == null || document.getData() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(document.getType()));
        headers.setContentDispositionFormData("attachment", document.getFileName());
        headers.setContentLength(document.getData().length);

        return new ResponseEntity<>(document.getData(), headers, HttpStatus.OK);
    }
}
