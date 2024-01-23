package com.souslesens.Jowl.model;

public class ontopRepoDataInput {

    private String ontologyURIEncoded64;
    public String getOntologyURIEncoded64() {
        return ontologyURIEncoded64;
    }

    private String ontologyContentEncoded64;
    public String getOntologyContentEncoded64() {
        return ontologyContentEncoded64;
    }

    private String mappingsEncoded64;
    public String getMappingsEncoded64() {
        return mappingsEncoded64;
    }

    public ontopRepoDataInput(String ontologyURIEncoded64, String ontologyContentEncoded64, String mappingsEncoded64) {
        this.ontologyURIEncoded64 = ontologyURIEncoded64;
        this.ontologyContentEncoded64 = ontologyContentEncoded64;
        this.mappingsEncoded64 = mappingsEncoded64;
    }
    
}
