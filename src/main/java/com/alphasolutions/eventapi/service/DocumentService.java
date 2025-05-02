package com.alphasolutions.eventapi.service;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {

    private final Tika tika;

    public DocumentService(Tika tika) {
        this.tika = tika;
    }

    public String extractTextFromFile(MultipartFile file) throws Exception {
        return tika.parseToString(file.getInputStream());
    }
}
