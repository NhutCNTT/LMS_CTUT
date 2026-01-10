package Project1.com.LibraryManagement.Service;

import Project1.com.LibraryManagement.Entity.Author;
import Project1.com.LibraryManagement.Entity.Books;
import Project1.com.LibraryManagement.Entity.Category;
import Project1.com.LibraryManagement.Entity.Users;
import Project1.com.LibraryManagement.Repository.AuthorRepos;
import Project1.com.LibraryManagement.Repository.BookRepos;
import Project1.com.LibraryManagement.Repository.CategoryRepos;
import Project1.com.LibraryManagement.Repository.UserRepos;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookImpl implements BookService {

    @Autowired
    private BookRepos bookRepos;

    @Autowired
    private UserRepos userRepos;

    @Autowired
    private AuthorRepos authorRepos;

    @Autowired
    private CategoryRepos categoryRepos;

    /* ================= CRUD ================= */

    @Override
    public Books saveBooks(Books books) {
        return bookRepos.save(books);
    }

    @Override
    public List<Books> getAllBooks() {
        return bookRepos.findAll();
    }

    @Override
    public Optional<Books> findById(Long id) {
        return bookRepos.findById(id);
    }

    @Override
    public Books getBookById(Long id) {
        return bookRepos.findById(id).orElse(null);
    }

    @Override
    public Boolean existsBooks(String bookCode, String bookName) {
        return bookRepos.existsByBookCodeAndBookName(bookCode, bookName);
    }

    @Override
    @Transactional
    public Long deleteBooks(Long id) {
        bookRepos.deleteById(id);
        return id;
    }

    /* ================= SORT & PAGINATION ================= */

    @Override
    @Cacheable(value = "book-field", key = "#field")
    public List<Books> findAllBook(String field) {
        return bookRepos.findAll(Sort.by(Sort.Direction.ASC, field));
    }

    @Override
    @Cacheable(value = "book-page", key = "'page-' + #offset + '-size-' + #pageSize")
    public Page<Books> findBookPagination(int offset, int pageSize) {
        return bookRepos.findAll(PageRequest.of(offset, pageSize));
    }

    /* ================= SEARCH SUGGEST ================= */

    @Override
    public List<String> suggest(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        return bookRepos.suggestKeyword(keyword.trim());
    }

    /* ================= EXPORT CSV ================= */

    @Override
    public void exportBooksToCsv(Writer writer) throws IOException {

        List<Books> books = bookRepos.findAll();

        CSVPrinter csvPrinter = new CSVPrinter(
                writer,
                CSVFormat.DEFAULT.withHeader(
                        "Book Code",
                        "Book Name",
                        "Author",
                        "ISBN",
                        "Category",
                        "Price",
                        "Status"
                )
        );

        for (Books b : books) {

            String authorNames = b.getAuthors().stream()
                    .map(Author::getName)
                    .collect(Collectors.joining(", "));

            csvPrinter.printRecord(
                    b.getBookCode(),
                    b.getBookName(),
                    authorNames,
                    b.getIsbn(),
                    b.getCategory() != null ? b.getCategory().getName() : "",
                    b.getPrice(),
                    b.getStatus()
            );
        }

        csvPrinter.flush();
    }

    @Override
    public void exportUsersToCsv(Writer writer) throws IOException {

        List<Users> users = userRepos.findAll();

        CSVPrinter csvPrinter = new CSVPrinter(
                writer,
                CSVFormat.DEFAULT.withHeader(
                        "ID",
                        "Full Name",
                        "Email",
                        "Phone Number",
                        "Address",
                        "Status",
                        "Roles"
                )
        );

        for (Users u : users) {
            csvPrinter.printRecord(
                    u.getId(),
                    u.getFullName(),
                    u.getEmail(),
                    u.getPhoneNumber(),
                    u.getAddress(),
                    u.getUserStatus(),
                    u.getRoles()
            );
        }

        csvPrinter.flush();
    }

    /* ================= IMPORT CSV ================= */

    @Override
    @Transactional
    public void importBooksFromCsv(MultipartFile file) throws IOException {

        List<Books> books = new ArrayList<>();

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)
                );
                CSVParser csvParser = new CSVParser(
                        reader,
                        CSVFormat.DEFAULT
                                .withFirstRecordAsHeader()
                                .withIgnoreHeaderCase()
                                .withTrim()
                )
        ) {
            for (CSVRecord record : csvParser) {

                Books b = new Books();
                b.setBookCode(record.get("Book Code"));
                b.setBookName(record.get("Book Name"));
                b.setIsbn(record.get("ISBN"));
                b.setPrice(Double.parseDouble(record.get("Price")));
                b.setStatus(record.get("Status"));
                b.setDescription("Imported from CSV");

                /* ===== CATEGORY ===== */
                String categoryName = record.get("Category");
                if (categoryName != null && !categoryName.isBlank()) {
                    Category category = categoryRepos.findByName(categoryName);
                    if (category == null) {
                        category = new Category();
                        category.setName(categoryName);
                        category = categoryRepos.save(category);
                    }
                    b.setCategory(category);
                }

                /* ===== AUTHORS ===== */
                String authorRaw = record.get("Author");
                List<Author> authorList = new ArrayList<>();

                if (authorRaw != null && !authorRaw.isBlank()) {
                    String[] names = authorRaw.split(",");
                    for (String name : names) {
                        String trimmedName = name.trim();
                        Author author = authorRepos.findByName(trimmedName);
                        if (author == null) {
                            author = new Author();
                            author.setName(trimmedName);
                            author = authorRepos.save(author);
                        }
                        authorList.add(author);
                    }
                }
                b.setAuthors((Set<Author>) authorList);

                books.add(b);
            }
        }

        if (!books.isEmpty()) {
            bookRepos.saveAll(books);
        }
    }

    @Override
    public Books findByBookCodeAndBookName(String bookCode, String bookName) {
        return bookRepos.findByBookCodeAndBookName(bookCode, bookName);
    }
}
