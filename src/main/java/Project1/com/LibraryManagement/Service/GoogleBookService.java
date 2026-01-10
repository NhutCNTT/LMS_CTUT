package Project1.com.LibraryManagement.Service;

import Project1.com.LibraryManagement.DTO.GoogleBookResponse;
import Project1.com.LibraryManagement.Entity.Author;
import Project1.com.LibraryManagement.Entity.Books;
import Project1.com.LibraryManagement.Entity.Category;
import Project1.com.LibraryManagement.Repository.AuthorRepos;
import Project1.com.LibraryManagement.Repository.CategoryRepos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GoogleBookService {

    private final WebClient webClient;
    private final AuthorRepos authorRepos;
    private final CategoryRepos categoryRepos;

    public Optional<Books> getBookByIsbn(String isbn) {

        GoogleBookResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q", "isbn:" + isbn)
                        .build())
                .retrieve()
                .bodyToMono(GoogleBookResponse.class)
                .block();

        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            return Optional.empty();
        }

        GoogleBookResponse.Item item = response.getItems().get(0);
        GoogleBookResponse.VolumeInfo info = item.getVolumeInfo();

        Books book = new Books();

        // ===== BASIC INFO =====
        book.setBookCode(item.getId());
        book.setBookName(info != null && info.getTitle() != null ? info.getTitle() : "No title");
        book.setIsbn(isbn);
        book.setDescription(info != null ? info.getDescription() : "");

        // ===== IMAGE =====
        String imageUrl = "default.jpg";
        if (info != null && info.getImageLinks() != null) {
            if (info.getImageLinks().getThumbnail() != null) {
                imageUrl = info.getImageLinks().getThumbnail();
            } else if (info.getImageLinks().getSmallThumbnail() != null) {
                imageUrl = info.getImageLinks().getSmallThumbnail();
            }
        }
        book.setImage(imageUrl);

        // ===== PRICE =====
        book.setPrice(0.0);

        // ===== AUTHORS =====
        List<Author> authors = new ArrayList<>();
        if (info != null && info.getAuthors() != null) {
            for (String name : info.getAuthors()) {
                String trimmed = name.trim();
                Author author = authorRepos.findByName(trimmed);
                if (author == null) {
                    author = new Author();
                    author.setName(trimmed);
                    author = authorRepos.save(author);
                }
                authors.add(author);
            }
        }

        if (authors.isEmpty()) {
            Author unknown = authorRepos.findByName("Unknown");
            if (unknown == null) {
                unknown = new Author();
                unknown.setName("Unknown");
                unknown = authorRepos.save(unknown);
            }
            authors.add(unknown);
        }
        book.setAuthors((Set<Author>) authors);

        // ===== CATEGORY =====
        if (info != null && info.getCategories() != null && !info.getCategories().isEmpty()) {
            String categoryName = info.getCategories().get(0);
            Category category = categoryRepos.findByName(categoryName);
            if (category == null) {
                category = new Category();
                category.setName(categoryName);
                category = categoryRepos.save(category);
            }
            book.setCategory(category);
        }

        return Optional.of(book);
    }
}
