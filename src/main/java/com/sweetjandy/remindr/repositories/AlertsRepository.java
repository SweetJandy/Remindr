package com.sweetjandy.remindr.repositories;

import com.sweetjandy.remindr.models.Alert;
import com.sweetjandy.remindr.models.Contact;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AlertsRepository extends CrudRepository<Alert, Long> {

    @Query(value = "SELECT * from alerts WHERE remindr_id = ?1", nativeQuery = true)
    List<Alert> findForRemindr(long id);

}
