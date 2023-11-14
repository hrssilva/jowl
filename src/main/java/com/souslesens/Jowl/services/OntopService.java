package com.souslesens.Jowl.services;
import com.souslesens.Jowl.model.ontopSparqlToSqlInput;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import it.unibz.inf.ontop.rdf4j.repository.OntopRepository;
import it.unibz.inf.ontop.rdf4j.repository.OntopRepositoryConnection;

import java.util.Base64;
import java.util.HashMap;


public interface OntopService {
    String ontopSPARQL2SQL(String reqEncoded64, OntopRepository repo);
    
    OntopRepository ontopInitRepo(ontopSparqlToSqlInput input);

    boolean healthCheck(HashMap<String, OntopRepository> repos);
    
    public static String decodeBase64(String encodedString) {
        byte[] decodedBytes = Base64.getMimeDecoder().decode(encodedString);
        return new String(decodedBytes);
    }
}
