package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.Result;
import com.alphasolutions.eventapi.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    public Result save(Result result) {
        return resultRepository.save(result);
    }
}
