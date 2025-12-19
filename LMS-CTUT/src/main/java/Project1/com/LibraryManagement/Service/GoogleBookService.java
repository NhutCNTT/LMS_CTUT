package Project1.com.LibraryManagement.Service;

import Project1.com.LibraryManagement.DTO.GoogleBookResponse;
import Project1.com.LibraryManagement.Entity.Books;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Service
public class GoogleBookService {
    private final WebClient webClient;

    public GoogleBookService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Optional<Books> getBookByIsbn(String isbn){
        GoogleBookResponse googleBookResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q","isbn:" + isbn)
                        .build())
                .retrieve()
                .bodyToMono(GoogleBookResponse.class)
                .block();
//default value
        if (googleBookResponse == null
                || googleBookResponse.getItems() == null
                || googleBookResponse.getItems().isEmpty()) {
            return Optional.empty();
        }
        GoogleBookResponse.Item item = googleBookResponse.getItems().get(0);
        GoogleBookResponse.VolumeInfo info = item.getVolumeInfo();
        Books books = new Books();
        String bookCode = item.getId() != null ? item.getId() : "";

        books.setBookCode(bookCode);


        books.setBookName(info != null && info.getTitle() != null ? info.getTitle() : "No Title");

        if (info != null && info.getAuthors() != null && !info.getAuthors().isEmpty()) {
            books.setAuthor(String.join(", ", info.getAuthors()));
        } else {
            books.setAuthor("No Author");
        }

        books.setDescription(info != null && info.getDescription() != null ? info.getDescription() : "");

        if (info != null && info.getCategories() != null && !info.getCategories().isEmpty()) {
            books.setCategory(info.getCategories().get(0));
        } else {
            books.setCategory("unknown");
        }

        books.setIsbn(isbn);

        String imageUrl = null;
        if (info != null && info.getImageLinks() != null) {
            if (info.getImageLinks().getThumbnail() != null && !info.getImageLinks().getThumbnail().isEmpty()) {
                imageUrl = info.getImageLinks().getThumbnail();
            } else if (info.getImageLinks().getSmallThumbnail() != null && !info.getImageLinks().getSmallThumbnail().isEmpty()) {
                imageUrl = info.getImageLinks().getSmallThumbnail();
            }
        }
        books.setImage(imageUrl != null ? imageUrl : "default.jpg");

        books.setPrice(0.0);
        books.setQuantity(1);
        books.setStatus("Đang lưu thông");
        books.setLocation("A-1-R-1-2");

        return Optional.of(books);
    }
}
