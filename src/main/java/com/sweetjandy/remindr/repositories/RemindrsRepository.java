package com.sweetjandy.remindr.repositories;


import com.sweetjandy.remindr.models.Alert;
import com.sweetjandy.remindr.models.Contact;
import com.sweetjandy.remindr.models.Remindr;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RemindrsRepository extends CrudRepository<Remindr, Long>{
    Remindr findByTitle(String title);

    @Query(value = "SELECT * from remindrs WHERE user_id = ?1", nativeQuery = true)
    List<Remindr> findForUser(long id);
}
