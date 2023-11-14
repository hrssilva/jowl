package com.souslesens.Jowl.services;
import com.souslesens.Jowl.model.ontopSparqlToSqlInput;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import it.unibz.inf.ontop.rdf4j.repository.OntopRepository;
import it.unibz.inf.ontop.rdf4j.repository.OntopRepositoryConnection;
import org.springframework.stereotype.Service;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import java.io.StringReader;
import java.util.Base64;

import java.util.HashMap;


@Service
public class OntopServiceImpl implements OntopService {
    
    @Override
    public String ontopSPARQL2SQL(String reqEncoded64, OntopRepository repo) {
        String req = OntopService.decodeBase64(reqEncoded64);

        try (OntopRepositoryConnection connection = repo.getConnection()) {
            connection.begin();
            try(TupleQueryResult tupleSql = connection.prepareTupleQuery(QueryLanguage.SPARQL, req).evaluate()){
                while(tupleSql.hasNext()){
                    BindingSet bindingSet = tupleSql.next();

                    for(String bindingName : tupleSql.getBindingNames()){
                        System.out.println(bindingName + " = " + bindingSet.getValue(bindingName));
                    }
                }
            }

            connection.commit();
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return "";
    }

    @Override
    public OntopRepository ontopInitRepo(ontopSparqlToSqlInput input) {
        String ontologyContent = OntopService.decodeBase64(input.getOntologyContentEncoded64());
        String mappings = OntopService.decodeBase64(input.getMappingsEncoded64());

        try {
            OntopSQLOWLAPIConfiguration config = OntopSQLOWLAPIConfiguration.defaultBuilder()
                    .ontologyFile(ontologyContent)
                    .nativeOntopMappingFile(mappings)
                    .build();

            OntopRepository ontopRepo = OntopRepository.defaultRepository(config);

            // verify later
            try (OntopRepositoryConnection connection = ontopRepo.getConnection()) {
                connection.begin();
                connection.add(new StringReader(ontologyContent), "", RDFFormat.TURTLE);
                connection.commit();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return ontopRepo;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean healthCheck(HashMap<String, OntopRepository> repos) {
        boolean allHealthy = true;

        for (HashMap.Entry<String, OntopRepository> entry : repos.entrySet()) {
            String repoName = entry.getKey();
            OntopRepository repo = entry.getValue();

            try (OntopRepositoryConnection connection = repo.getConnection()) {
                System.out.println("Repo " + repoName + "' ok ");
            } catch (Exception e) {
                allHealthy = false;
                System.err.println("Error connecting to '" + repoName + "': " + e.getMessage());
            }
        }

        return allHealthy;
    }

}
