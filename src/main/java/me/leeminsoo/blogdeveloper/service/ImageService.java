package me.leeminsoo.blogdeveloper.service;

import lombok.RequiredArgsConstructor;

import me.leeminsoo.blogdeveloper.domain.ArticleImage;
import me.leeminsoo.blogdeveloper.repository.ImageRepository;
import org.springframework.stereotype.Service;


import java.util.List;



@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public List<ArticleImage> getImagesByArticleId(Long id) {
        return imageRepository.findByArticleId(id);
    }

    public List<ArticleImage> findRecentImages() {
        return imageRepository.findTop10ByOrderById();
    }

}
