package com.example.skillsh.services.review;

import com.example.skillsh.domain.dto.review.AddReviewRequest;
import com.example.skillsh.domain.dto.review.ReviewAdminDto;
import com.example.skillsh.domain.entity.Review;
import com.example.skillsh.domain.entity.User;
import com.example.skillsh.repository.ReviewRepo;
import com.example.skillsh.repository.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminReviewService {

    private final ReviewRepo reviewRepository;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    public AdminReviewService(ReviewRepo reviewRepository, UserRepo userRepo, ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
    }

    public List<ReviewAdminDto> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // --- СИГУРЕН МЕТОД ЗА МАПВАНЕ ---
    private ReviewAdminDto convertToDto(Review review) {
        ReviewAdminDto dto = new ReviewAdminDto();
        dto.setId(review.getId());
        dto.setReviewText(review.getReviewText());
        dto.setRating(review.getRating());
        dto.setDateOfReview(review.getDateOfReview());

        // 🛠️ ТУК Е КЛЮЧЪТ: Проверяваме обектите и взимаме username
        // Увери се, че в Review.java полетата ти се казват reviewingUser и reviewedUser
        if (review.getReviewingUser() != null) {
            dto.setReviewingUserUsername(review.getReviewingUser().getUsername());
        }

        if (review.getReviewedUser() != null) {
            dto.setReviewedUserUsername(review.getReviewedUser().getUsername());
        }

        return dto;
    }

    // --- 2. CREATE ---
    @Transactional
    public ReviewAdminDto createReview(AddReviewRequest dto) {
        Review review = new Review();
        review.setReviewText(dto.getContent());
        review.setDateOfReview(dto.getDate());

        if (dto.getReviewerUsername() != null) {
            userRepo.findUserByUsername(dto.getReviewerUsername()).ifPresent(review::setReviewingUser);
        }
        if (dto.getTargetUsername() != null) {
            userRepo.findUserByUsername(dto.getTargetUsername()).ifPresent(review::setReviewedUser);
        }

        Review savedReview = reviewRepository.save(review);
        return convertToDto(savedReview);
    }

    // 3. UPDATE
    @Transactional
    public Review updateReview(Long id, AddReviewRequest dto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ревюто не е намерено!"));

        mapDtoToEntity(dto, review);
        return reviewRepository.save(review);
    }

    // 4. DELETE
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Ревюто не съществува!");
        }
        reviewRepository.deleteById(id);
    }

    // Помощен метод за мапване
    private void mapDtoToEntity(AddReviewRequest dto, Review review) {
        review.setReviewText(dto.getContent());

        if (dto.getDate() != null) {
            review.setDateOfReview(dto.getDate());
        }

        // Намираме кой пише ревюто (Reviewer)
        if (dto.getReviewerUsername() != null) {
            User reviewer = userRepo.findUserByUsername(dto.getReviewerUsername())
                    .orElseThrow(() -> new RuntimeException("Потребителят (оценяващ) не е намерен!"));
            review.setReviewingUser(reviewer);
        }

        // Намираме за кого е ревюто (Reviewed / Target)
        if (dto.getTargetUsername() != null) {
            User target = userRepo.findUserByUsername(dto.getTargetUsername())
                    .orElseThrow(() -> new RuntimeException("Потребителят (оценяван) не е намерен!"));
            review.setReviewedUser(target);
        }

        // ВАЖНО: Ако добавиш 'int rating;' в AddReviewRequest.java, разкоментирай долния ред:
        // review.setRating(dto.getRating());
    }
}
