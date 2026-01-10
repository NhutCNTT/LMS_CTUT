package Project1.com.LibraryManagement.Service;

import Project1.com.LibraryManagement.Entity.PinBooks;
import Project1.com.LibraryManagement.Repository.PinBookRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PinBookImpl implements PinBookService {
    @Autowired
    public PinBookRepos pinBookRepos;

    @Override
    public List<PinBooks> findUserById(Long id) {
        return pinBookRepos.findByUsers_Id(id);
    }

    @Transactional
    @Override
    public void deletePin(Long id) {
        pinBookRepos.deleteById(id);
    }


}
