package Project1.com.LibraryManagement.Service;
import Project1.com.LibraryManagement.Entity.Books;
import Project1.com.LibraryManagement.Entity.Users;
import Project1.com.LibraryManagement.Repository.BookRepos;
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

import java.awt.print.Book;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service

public class BookImpl implements BookService {
    @Autowired
    public BookRepos bookRepos;
    @Autowired
    public UserRepos userRepos;


    @Override
    public Books saveBooks(Books books) {
        return bookRepos.save(books);
    }

    @Override
    public List<Books> getAllBooks() {
        return bookRepos.findAll();
    }

    @Override
    public Boolean existsBooks(String bookCode, String bookName) {
        return bookRepos.existsByBookCodeAndBookName(bookCode,bookName);
    }

    @Override
    @Transactional
    public Long deleteBooks(Long id) {
        bookRepos.deleteById(id);
        return id;
    }

    @Override
    public Optional<Books> findById(Long id) {
        return bookRepos.findById(id);
    }

    @Override
    @Cacheable(value = "book-field",key = "#field")
    public List<Books> findAllBook(String field) {
        return bookRepos.findAll(Sort.by(Sort.Direction.ASC,field));
    }

    @Override
    @Cacheable(value = "book-page", key = "'page-' + #offset + '-size-' + #pageSize")
    public Page<Books> findBookPagination(int offset, int pageSize) {
        Page<Books> booksPage = bookRepos.findAll(PageRequest.of(offset,pageSize));
        return booksPage;
    }
    @Override
    public Books getBookById(Long id) {
        return bookRepos.findById(id).orElse(null);
    }

    @Override
    public List<String> suggest(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        return bookRepos.suggestKeyword(keyword.trim());
    }

    @Override
    public void exportBooksToCsv(Writer writer) throws IOException {
        List<Books> books = bookRepos.findAll();
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("Book Code", "Book Name", "Author", "ISBN", "Category", "Price", "Quantity", "Status", "Location"));
        for (Books b : books) {
            csvPrinter.printRecord(b.getBookCode(), b.getBookName(), b.getAuthor(), b.getIsbn(),
                    b.getCategory(), b.getPrice(), b.getQuantity(), b.getStatus(), b.getLocation());
        }
        csvPrinter.flush();
    }
    @Override
    public void exportUsersToCsv(Writer writer) throws IOException {
        List<Users> users = userRepos.findAll();
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("id", "FullName", "Email", "PhoneNumber", "Address", "Falcuties", "Status", "Roles"));
        for (Users u : users) {
            csvPrinter.printRecord(u.getId(), u.getFullName(), u.getEmail(), u.getPhoneNumber(),
                    u.getAddress(), u.getFaculties(), u.getUserStatus(), u.getRoles());
        }
        csvPrinter.flush();
    }
    @Override
    @Transactional
    public void importBooksFromCsv(MultipartFile file) throws IOException {
        List<Books> books = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(
                     reader,
                     CSVFormat.DEFAULT
                             .withFirstRecordAsHeader()
                             .withIgnoreHeaderCase()
                             .withTrim()
             )) {

            for (CSVRecord record : csvParser) {
                Books b = new Books();
                b.setBookCode(record.get("Book Code"));
                b.setBookName(record.get("Book Name"));
                b.setAuthor(record.get("Author"));
                b.setIsbn(record.get("ISBN"));
                b.setCategory(record.get("Category"));
                b.setPrice(Double.parseDouble(record.get("Price")));
                b.setQuantity(Integer.parseInt(record.get("Quantity")));
                b.setStatus(record.get("Status"));
                b.setLocation(record.get("Location"));
                b.setDescription("Imported from CSV");

                books.add(b);
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("Cấu trúc file CSV không đúng. Vui lòng kiểm tra tên cột/header.", e);
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
