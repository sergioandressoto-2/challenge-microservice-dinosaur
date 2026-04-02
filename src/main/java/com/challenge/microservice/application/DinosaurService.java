package com.challenge.microservice.application;


import com.challenge.microservice.domain.Dinosaur;
import com.challenge.microservice.application.dto.DinosaurRequest;
import com.challenge.microservice.application.dto.DinosaurResponse;
import com.challenge.microservice.port.db.DbPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DinosaurService {

    private static final Logger log = LoggerFactory.getLogger(DinosaurService.class);

    @Autowired
    private DbPort dbPort;

    public String createDinosaur(DinosaurRequest dinosaurReq) {
        log.info("dinosaur object receive: {}, {}", dinosaurReq.getDiscoveryDate(), dinosaurReq.getName());
        // TODO validar los datos de entrada
        // TDOD Regla: Nombre único (Validación de contexto)
        /*
        if (repository.existsByName(command.name())) {
            throw new AlreadyExistsException("Dinosaur name already taken.");
        }
        */
        Dinosaur dinosaur = new Dinosaur (
                dinosaurReq.getName(),
                dinosaurReq.getSpecies(),
                dinosaurReq.getDiscoveryDate(),
                dinosaurReq.getExtinctionDate()
        );
        dbPort.create(dinosaur);
        return "OK";
    }

    /*
    public DinosaurResponse updateDinosaur(DinosaurRequest dinosaur) {

        return "";
    }
    */

    public List<DinosaurResponse> returnDinosaurs() {
        log.info("Return list of dinosarus from db");
        return dbPort.returnDinosaurs();
    }


    public Optional<DinosaurResponse> returnDinosaur(String idDinosaur) {
        // findbyid if existe return a object found.
        log.info("Return a dinosarus from db");
        return dbPort.getDinosaur(idDinosaur);
    }

    /*

     public String deleteDinosaur(String idDinosaur) {

    }
 */


}
