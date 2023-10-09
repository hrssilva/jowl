package com.souslesens.Jowl.model;

import it.unibz.inf.ontop.injection.OntopSQLOWLAPIConfiguration;
import it.unibz.inf.ontop.rdf4j.repository.OntopRepository;
import it.unibz.inf.ontop.rdf4j.repository.OntopRepositoryConnection;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import java.util.HashMap;

public class ontopSparqlToSql {
    HashMap<String, OntopRepository> repos;

    /* Returns the repository storage object (java.util.HashMap) */
    public HashMap<String, OntopRepository> getRepos() {
        return repos;
    }

    /* Return the repository associated with the given IRI */
    public OntopRepository getRepo(String IRI) {
        return repos.get(IRI);
    }

    /* Add a repository if it does not exist already */
    public void addRepo(String IRI, OntopRepository repo) {
        repos.computeIfAbsent(IRI, k ->  repo);
    }

    /* Add a repository or substitute the already stored repository */
    public void setRepo(String IRI, OntopRepository repo) {
        repos.put(IRI, repo);
    }

}
