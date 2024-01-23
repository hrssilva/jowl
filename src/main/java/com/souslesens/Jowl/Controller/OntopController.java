package com.souslesens.Jowl.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.souslesens.Jowl.services.OntopService;
import com.souslesens.Jowl.model.ontopSparqlToSql;
import com.souslesens.Jowl.model.ontopSparqlToSqlInput;
import com.souslesens.Jowl.model.ontopRepoDataInput;

import it.unibz.inf.ontop.rdf4j.repository.OntopRepository;
import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/sparql2sql")
public class OntopController {

  @Autowired
  OntopService ontopService;

  ontopSparqlToSql sparql2sqlModel;
  
  @PostConstruct
  public void initModel() {
    this.sparql2sqlModel = new ontopSparqlToSql();
  }

  @PostMapping("/a")
  public ResponseEntity<?> sparql2sql(@RequestBody ontopSparqlToSqlInput request) {

    String reqEncoded64 = request.getSparqlReqEncoded64();
    String repoURI = OntopService.decodeBase64(request.getOntologyURIEncoded64());

    // Check for HealthCheck request
    if ("http://check.com/healthcheck".equals(repoURI)) {
      if (ontopService.healthCheck(sparql2sqlModel.getRepos())) {
        return ResponseEntity.ok("OK");

      }
    }

    OntopRepository repo = sparql2sqlModel.getRepo(repoURI);

    // Add parameter count check?

    // check if the repo not exists
    if (repo == null) {
        return new ResponseEntity<>("Repository not exists", HttpStatus.CONFLICT);
    }

    // create sql request - OntopService.ontopSPARQL2SQL(String reqEncoded64, OntopRepository repo);
    String response = ontopService.ontopSPARQL2SQL(reqEncoded64, repo);
    
    return ResponseEntity.ok(response);

  }
  @PostMapping("/b")
  public ResponseEntity<?> createRepo(@RequestBody ontopRepoDataInput request) {

    // decode the parameters from the request 
    String ontologyURI = OntopService.decodeBase64(request.getOntologyURIEncoded64());
    
    // check if the repo already exists
    OntopRepository existingRepo = sparql2sqlModel.getRepo(ontologyURI);
    if (existingRepo != null) {
        return new ResponseEntity<>("Repository already exists", HttpStatus.CONFLICT);
    }

    // create repo
    OntopRepository newRepo = ontopService.ontopInitRepo(request);

    // Add repo do hash
    sparql2sqlModel.addRepo(ontologyURI, newRepo);

    return new ResponseEntity<>("Ok!", HttpStatus.OK);
  }


  private int countParams(Object... parameters) {
    int count = 0;
    for (Object param : parameters) {
      if (param != null && !param.toString().isEmpty()) {
        count++;
      }
    }
    return count;
  }

}
