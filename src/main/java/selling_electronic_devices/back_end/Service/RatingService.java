package selling_electronic_devices.back_end.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import selling_electronic_devices.back_end.Dto.RatingDto;
import selling_electronic_devices.back_end.Entity.Rating;
import selling_electronic_devices.back_end.Repository.RatingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    public String createRating(RatingDto ratingDto) {
        Rating rating = new Rating();
        rating.setRatingId(UUID.randomUUID().toString());
        rating.setUserId(ratingDto.getUserId());
        rating.setProductId(ratingDto.getProductId());
        rating.setRating(ratingDto.getRating());
        rating.setComment(ratingDto.getComment());

        ratingRepository.save(rating);
        return "Created rating successfully.";
    }


    public List<Rating> getRatingsByProduct(String productId) {
        return ratingRepository.findByProductId(productId);
    }

    public boolean updateRating(String ratingId, RatingDto ratingDto) {
        Optional<Rating> optionalRating = ratingRepository.findById(ratingId);
        if (optionalRating.isPresent()) {
            Rating rating = new Rating();
            rating.setUserId(ratingDto.getUserId());
            rating.setProductId(ratingDto.getProductId());
            rating.setComment(ratingDto.getComment());
            rating.setRating(ratingDto.getRating());

            ratingRepository.save(rating);
            return true;
        }
        return false;
    }

    public boolean deleteRating(String ratingId) {
        Optional<Rating> optionalRating = ratingRepository.findById(ratingId);
        //optionalRating.ifPresent(rating -> ratingRepository.delete(rating));
        if (optionalRating.isPresent()) {
            ratingRepository.delete(optionalRating.get());
            return true;
        }
        return false;
    }
}
