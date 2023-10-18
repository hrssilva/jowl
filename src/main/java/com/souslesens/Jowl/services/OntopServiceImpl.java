package com.souslesens.Jowl.services;
import com.souslesens.Jowl.model.ontopSparqlToSqlInput;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import it.unibz.inf.ontop.rdf4j.repository.OntopRepository;
import it.unibz.inf.ontop.rdf4j.repository.OntopRepositoryConnection;
import org.springframework.stereotype.Service;
import java.io.StringReader;
import java.util.Base64;

@Service
public class OntopServiceImpl implements OntopService {
    
    @Override
    public String ontopSPARQL2SQL(String reqEncoded64, OntopRepository repo) {
        // TODO
        return "";
    }

    @Override
    public OntopRepository ontopInitRepo(ontopSparqlToSqlInput input) {
        String ontologyURI = decodeBase64(input.getOntologyURIEncoded64());
        String ontologyContent = decodeBase64(input.getOntologyContentEncoded64());
        String mappings = decodeBase64(input.getMappingsEncoded64());

        try {
            OntopSQLOWLAPIConfiguration config = OntopSQLOWLAPIConfiguration.defaultBuilder()
                    .ontologyFile(ontologyURI)
                    .nativeOntopMappingFile(mappings)
                    .build();

            OntopRepository ontopRepo = OntopRepository.defaultRepository(config);

            // verify later
            try (OntopRepositoryConnection connection = ontopRepo.getConnection()) {
                connection.begin();
                connection.add(new StringReader(ontologyContent), "", RDFFormat.TURTLE);
                connection.commit();
            }

            return ontopRepo;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String decodeBase64(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes);
    }
}