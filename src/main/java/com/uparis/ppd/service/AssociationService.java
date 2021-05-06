package com.uparis.ppd.service;

import com.uparis.ppd.model.Association;
import com.uparis.ppd.repository.AssociationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AssociationService {

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private FormatService formatService;

    public Association create(String name, String description, String associationPrice1Month, String associationPrice3Months, String associationPrice12Months) {
        if (getByName(name) == null) {
            Association association = new Association(name, description, formatService.formatPrice(associationPrice1Month), formatService.formatPrice(associationPrice3Months), formatService.formatPrice(associationPrice12Months));
            update(association);
            return association;
        } else {
            return null;
        }
    }

    public void update(Association association) {
        associationRepository.save(association);
    }

    public Association getByName(String name) {
        Optional<Association> association = Optional.ofNullable(associationRepository.findByName(name));
        return association.orElse(null);
    }
}
