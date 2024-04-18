package com.souslesens.Jowl.services;

import com.souslesens.Jowl.model.ontopSparqlToSqlInput;
import com.souslesens.Jowl.model.ontopRepoDataInput;

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
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import java.io.StringReader;
import java.util.Base64;

import java.util.HashMap;
import org.json.JSONObject;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

@Service
public class OntopServiceImpl implements OntopService {
  private static final String propertiesFile = "src/main/resources/ontop.properties";

  @Override
  public String ontopSPARQL2SQL(String reqEncoded64, OntopRepository repo) {
    String req = OntopService.decodeBase64(reqEncoded64);

    try {
      OntopRepositoryConnection connection = repo.getConnection();
      // TupleQueryResult tupleData = connection.prepareTupleQuery(QueryLanguage.SPARQL, req).evaluate();
      // // connection.begin();
      // while (tupleData.hasNext()) {
      //   BindingSet bindingSet = tupleData.next();
      //   for (String bindingName : tupleData.getBindingNames()) {
      //     System.out.println(bindingName + " = " + bindingSet.getValue(bindingName));
      //   }
      // }
      // // connection.commit();
      return connection.reformulateIntoNativeQuery(req);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public String ontopSPARQL2SQL2(String reqEncoded64, OntopRepository repo) {
    String req = OntopService.decodeBase64(reqEncoded64);

    try {
      OntopRepositoryConnection connection = repo.getConnection();
      TupleQueryResult tupleData = connection.prepareTupleQuery(QueryLanguage.SPARQL, req).evaluate();
      // // connection.begin();
      List<JSONObject> targetList = new ArrayList<>();
      while (tupleData.hasNext()) {
        JSONObject selectObject = new JSONObject();
        BindingSet bindingSet = tupleData.next();
        for (String bindingName : tupleData.getBindingNames()) {
          //System.out.println(bindingName + " = " + bindingSet.getValue(bindingName));
          selectObject.put(bindingName, bindingSet.getValue(bindingName).stringValue());
        }
        targetList.add(selectObject);
      }
      // // connection.commit();

      JSONObject jsonObject = new JSONObject();
      jsonObject.put("bindings", tupleData.getBindingNames());
      jsonObject.put("result", targetList);
      String jsonString = jsonObject.toString();
      return jsonString;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }


  @Override
  public OntopRepository ontopInitRepo(ontopRepoDataInput input) {
    String ontologyContent = OntopService.decodeBase64(input.getOntologyContentEncoded64());
    String mappings = OntopService.decodeBase64(input.getMappingsEncoded64());

    try {
      OntopSQLOWLAPIConfiguration config = OntopSQLOWLAPIConfiguration.defaultBuilder()
          .ontologyReader(new StringReader(ontologyContent))
          .r2rmlMappingReader(new StringReader(mappings))
          .propertyFile(propertiesFile)
          .build();

      OntopRepository ontopRepo = OntopRepository.defaultRepository(config);

      // verify later
      try (OntopRepositoryConnection connection = ontopRepo.getConnection()) {
        // connection.begin();
        // connection.add(new StringReader(ontologyContent), "", RDFFormat.TURTLE);
        // connection.commit();
        // connection.close();
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

  @Override
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
