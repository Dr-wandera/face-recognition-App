package com.kibabii_project.face_recognition.Client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FaceRecognitionClient {

    private final WebClient.Builder webClientBuilder;


}
