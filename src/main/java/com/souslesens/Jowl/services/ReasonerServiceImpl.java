package com.souslesens.Jowl.services;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEntityAxiomGenerator;
import org.semanticweb.owlapi.util.InferredIndividualAxiomGenerator;
import org.semanticweb.owlapi.util.InferredInverseObjectPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Service;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.souslesens.Jowl.model.reasonerConsistency;
import com.souslesens.Jowl.model.reasonerExtractTriples;
import com.souslesens.Jowl.model.reasonerInference;
import com.souslesens.Jowl.model.reasonerUnsatisfaisability;
@Service

public class ReasonerServiceImpl implements ReasonerService{
	
	
	
	 @Override
		public String getUnsatisfaisableClasses(String filePath, String Url) throws Exception {
	        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		     OWLOntology ontology = null ;
		        if (filePath == null && Url.isEmpty() == false && (Url.startsWith("http") || Url.startsWith("ftp"))) {
		        	
		        	ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
		        } else if(filePath.isEmpty() == false && Url == null) {
		        	
		            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
		        } else {
		        	return null;
		        }

	        PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
	        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

	        reasonerUnsatisfaisability myData = new reasonerUnsatisfaisability();
	        Node<OWLClass> unsatisfiableClasses = reasoner.getUnsatisfiableClasses();

	        OWLClass[] unsatisfiable = new OWLClass[unsatisfiableClasses.getSize()];
	        int i = 0;
	        if (unsatisfiableClasses.getSize() > 0) {
	            for (OWLClass cls : unsatisfiableClasses) {
	                unsatisfiable[i] = cls;
	                i++;
	            }
	            myData.setUnsatisfaisable(unsatisfiable);
	        }

	        OWLClass[] unsatisfiableArray = myData.getUnsatisfaisable();
	        String[] iriStrings = new String[unsatisfiableArray.length];
	        for (int j = 0; j < unsatisfiableArray.length; j++) {
	            iriStrings[j] = unsatisfiableArray[j].toStringID();
	        }
	        JSONObject jsonObject = new JSONObject();
	        jsonObject.put("unsatisfiable", iriStrings);
	        String jsonString = jsonObject.toString();
	        return jsonString;
	    }
	 // TEST

	 @Override
	 public String postInferencesContent(String ontologyContentDecoded64) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception {
	     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	     OWLOntology ontology = null ;
	     String filePath = null ;
	     if ( ontologyContentDecoded64.isEmpty() == false) {
	    	 InputStream ontologyStream = new ByteArrayInputStream(ontologyContentDecoded64.getBytes());
	    	 try {
	    		    Files.copy(ontologyStream, Paths.get("output.owl"), StandardCopyOption.REPLACE_EXISTING);
	    		    filePath = "output.owl";
	    		    
	    		} catch (IOException e) {
	    		    e.printStackTrace();
	    		}
	    	  ontology = manager.loadOntologyFromOntologyDocument(ontologyStream);
	    	
	     }
	     	if(filePath.isEmpty() == false) {
	        	
	            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
	        } else {
	        	return null;
	        }
	        PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
	        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
	        String fileName = "inferred-ontology.owl";
	        
	        reasoner.precomputeInferences(InferenceType.values());
	        OWLOntology inferredOntology = manager.createOntology();
	        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner);
	        iog.addGenerator(new InferredEquivalentClassesAxiomGenerator());
	        iog.addGenerator(new SameIndividualAxiomGenerator()); // Add custom generator for same individual axioms
	        iog.addGenerator(new InferredDifferentIndividualAxiomGenerator()); // Add custom generator for different individual axioms
	        iog.addGenerator(new InferredIntersectionOfAxiomGenerator());
	        iog.addGenerator(new InferredUnionOfAxiomGenerator());
	        iog.addGenerator(new InferredDisjointClassesAxiomGenerator());
	        OWLDataFactory dataFactory = manager.getOWLDataFactory();
	        iog.fillOntology(dataFactory, inferredOntology);
	        manager.saveOntology(inferredOntology, new OWLXMLOntologyFormat(), IRI.create(new File("inferred.owl")));
	        System.out.println("Infered Ontologie \n"+inferredOntology);
	        //////////////////////// TEST 

	        ///////////////////////// TEST
	     // Extract the specified axioms and expressions
	        JSONObject jsonObject = new JSONObject();
	        for (AxiomType<?> axiomType : AxiomType.AXIOM_TYPES) {
	            Set<? extends OWLAxiom> axioms = inferredOntology.getAxioms(axiomType);
	            System.out.println(convertAxiomSetToJSONArray(axioms));
	            System.out.println(axiomType.toString());
	            if (!axioms.isEmpty()) {
	                jsonObject.put(axiomType.toString(), convertAxiomSetToJSONArray(axioms));
	            }
	        }

	        // Extract the specified expressions
	        JSONArray expressionsArray = new JSONArray();
//	        for (OWLClassExpression classExpression : inferredOntology.getClassesInSignature()) {
////	            if (classExpression instanceof OWLObjectIntersectionOf ||
////	                classExpression instanceof OWLObjectUnionOf ||
////	                classExpression instanceof OWLObjectSomeValuesFrom ||
////	                classExpression instanceof OWLObjectAllValuesFrom ||
////	                classExpression instanceof OWLObjectHasValue ||
////	                classExpression instanceof OWLObjectMinCardinality ||
////	                classExpression instanceof OWLObjectExactCardinality ||
////	                classExpression instanceof OWLObjectMaxCardinality ||
////	                classExpression instanceof OWLObjectComplementOf ||
////	                classExpression instanceof OWLObjectHasSelf ||
////	                classExpression instanceof OWLObjectOneOf) {
//	                expressionsArray.put(classExpression.toString());
////	            }
//	        }
	        // EDIT1
//	        for (OWLClassExpression classExpression : inferredOntology.getClassesInSignature()) {
//	            if (classExpression instanceof OWLClass) {
//	                OWLClass namedClass = (OWLClass) classExpression;
//	                String className = namedClass.getIRI().toString();
//	                expressionsArray.put(className);
//	            }
//	        }
	        //EDIT2
	        for (OWLClassExpression classExpression : inferredOntology.getClassesInSignature()) {
	  	      
	        	
                String className = classExpression.toString();
                System.out.println("HOLY"+className);
                expressionsArray.put(className);
                IRI targetIRI = IRI.create(className);
             // Load the individual (assuming it's an individual)
                OWLNamedIndividual targetIndividual = manager.getOWLDataFactory().getOWLNamedIndividual(targetIRI);

                // Retrieve and print object property assertions for the individual
                Set<OWLObjectPropertyAssertionAxiom> objectPropertyAssertions = inferredOntology.getObjectPropertyAssertionAxioms(targetIndividual);
                System.out.println("Object property assertions for " + targetIRI + ":");
                for (OWLObjectPropertyAssertionAxiom axiom : objectPropertyAssertions) {
                    System.out.println(axiom);
                }
            }
        
	        jsonObject.put("owlexpressions", expressionsArray);
	        // Extract individuals' information
	        JSONArray individualsArray = new JSONArray();
	        for (OWLNamedIndividual individual : ontology.getIndividualsInSignature()) {
	            JSONObject individualInfo = new JSONObject();
	            individualInfo.put("name", individual.toString());

	            // Get the object property assertions for each individual
	            JSONArray objectPropertyAssertions = new JSONArray();
	            NodeSet<OWLNamedIndividual> objectPropertyTargets = reasoner.getObjectPropertyValues(individual, dataFactory.getOWLObjectProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()));
	            for (OWLNamedIndividual target : objectPropertyTargets.getFlattened()) {
	                objectPropertyAssertions.put(individual + " -> " + target);
	            }
	            individualInfo.put("objectPropertyAssertions", objectPropertyAssertions);

	            // Get the class assertions (types) for each individual
	            // Iterate over the set of class assertions in the ontology
	            for (OWLClassAssertionAxiom classAssertion : ontology.getAxioms(AxiomType.CLASS_ASSERTION)) {
	                if (classAssertion.getIndividual().equals(individual)) {
	                    OWLClassExpression assertedClass = classAssertion.getClassExpression();
	                    System.out.println("- " + assertedClass.toString());
	                }
	            }
	            JSONArray classAssertions = new JSONArray();
	            NodeSet<OWLClass> types = reasoner.getTypes(individual, false);
	            for (OWLClass owlClass : types.getFlattened()) {
	                classAssertions.put(individual + " rdf:type " + owlClass);
	            }
	            individualInfo.put("classAssertions", classAssertions);

	            individualsArray.put(individualInfo);
	        }
	        jsonObject.put("individuals", individualsArray);

	        String jsonString = jsonObject.toString();
	        System.out.println(jsonString);
	        return jsonString;
	        }

	        private static JSONArray convertAxiomSetToJSONArray(Set<? extends OWLAxiom> axiomSet) {
	            JSONArray jsonArray = new JSONArray();
	            for (OWLAxiom axiom : axiomSet) {
	                jsonArray.put(axiom.toString());
	            }
	            return jsonArray;
	        }
	 

	 @Override
	 public String postUnsatisfaisableClassesContent(String ontologyContentDecoded64) throws Exception {
	     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	     OWLOntology ontology = null ;
	     String filePath = null ;
	     if ( ontologyContentDecoded64.isEmpty() == false) {
	    	 InputStream ontologyStream = new ByteArrayInputStream(ontologyContentDecoded64.getBytes());
	    	 try {
	    		    Files.copy(ontologyStream, Paths.get("output.owl"), StandardCopyOption.REPLACE_EXISTING);
	    		    filePath = "output.owl";
	    		    
	    		} catch (IOException e) {
	    		    e.printStackTrace();
	    		}
	    	  ontology = manager.loadOntologyFromOntologyDocument(ontologyStream);
	    	
	     }
	     	if(filePath.isEmpty() == false) {
	        	
	            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
	        } else {
	        	return null;
	        }
	        PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
	        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

	        reasonerUnsatisfaisability myData = new reasonerUnsatisfaisability();
	        Node<OWLClass> unsatisfiableClasses = reasoner.getUnsatisfiableClasses();

	        OWLClass[] unsatisfiable = new OWLClass[unsatisfiableClasses.getSize()];
	        int i = 0;
	        if (unsatisfiableClasses.getSize() > 0) {
	            for (OWLClass cls : unsatisfiableClasses) {
	                unsatisfiable[i] = cls;
	                i++;
	            }
	            myData.setUnsatisfaisable(unsatisfiable);
	        }

	        OWLClass[] unsatisfiableArray = myData.getUnsatisfaisable();
	        String[] iriStrings = new String[unsatisfiableArray.length];
	        for (int j = 0; j < unsatisfiableArray.length; j++) {
	            iriStrings[j] = unsatisfiableArray[j].toStringID();
	        }
	        JSONObject jsonObject = new JSONObject();
	        jsonObject.put("unsatisfiable", iriStrings);
	        String jsonString = jsonObject.toString();
	        return jsonString;
	    }
	 
	 @Override
	 public String postConsistencyContent(String ontologyContentDecoded64) throws OWLOntologyCreationException, JsonProcessingException,Exception {
	     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	     OWLOntology ontology = null ;
	     if ( ontologyContentDecoded64.isEmpty() == false) {
	    	 System.out.println("From HERe \n"+ontologyContentDecoded64);
	    	 InputStream ontologyStream = new ByteArrayInputStream(ontologyContentDecoded64.getBytes());
	    	 try {
	    		    Files.copy(ontologyStream, Paths.get("output.owl"), StandardCopyOption.REPLACE_EXISTING);
	    		    String filePath = "output.owl";
	    		} catch (IOException e) {
	    		    e.printStackTrace();
	    		}
	    	  ontology = manager.loadOntologyFromOntologyDocument(ontologyStream);
	    	
	     }
	       
	         PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
	         OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
	         reasonerConsistency myData = new reasonerConsistency();
	         boolean consistency = reasoner.isConsistent();
	         System.out.println(consistency);
	         myData.setConsistency(consistency);
	         JSONObject jsonObject = new JSONObject();
	         jsonObject.put("consistency", myData.getConsistency());
	         String jsonString = jsonObject.toString();
	         return jsonString;
	         
	         
	 	}
	 
	 
	 
	 @Override
	 public String postInferences(String filePath, String url) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception {
	     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	     OWLOntology ontology = null ;

	        if (filePath == null && url.isEmpty() == false && (url.startsWith("http") || url.startsWith("ftp"))) {
	        	
	        	ontology = manager.loadOntologyFromOntologyDocument(IRI.create(url));
	        } else if(filePath.isEmpty() == false && url == null) {
	        	
	            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
	        } else {
	        	return null;
	        }
	        PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
	        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
	        String fileName = "inferred-ontology.owl";
	        reasoner.precomputeInferences(InferenceType.values());
	        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner);
	        OWLOntology inferredOntology = manager.createOntology();
	        OWLDataFactory dataFactory = manager.getOWLDataFactory();
	        iog.fillOntology(dataFactory, inferredOntology);
	        
	        List<OWLAxiom> axioms = new ArrayList<>();
	        for (OWLAxiom axiom : inferredOntology.getAxioms()) {
	            if (axiom instanceof OWLSubClassOfAxiom ||
	                axiom instanceof OWLDisjointClassesAxiom ||
	                axiom instanceof OWLEquivalentClassesAxiom ||
	                axiom instanceof OWLSubObjectPropertyOfAxiom ||
	                axiom instanceof OWLObjectPropertyDomainAxiom ||
	                axiom instanceof OWLObjectPropertyRangeAxiom ||
	                axiom instanceof OWLObjectPropertyAssertionAxiom ||
	                axiom instanceof OWLClassAssertionAxiom ||
	                axiom instanceof OWLDataPropertyAssertionAxiom) {
	                axioms.add(axiom);
	            }
	        }
	        
	        
	        StringBuilder sb = new StringBuilder();
	        // Add the inferred axioms to the list
	        for (OWLAxiom axiom : inferredOntology.getAxioms()) {
	        	sb.append(axiom.toString());
	            
	        }
	        String axiomsString = sb.toString();
	        JSONObject jsonObject = new JSONObject();
	        jsonObject.put("inference", axiomsString);
	         String jsonString = jsonObject.toString();
	         return jsonString;
	    }
	 
	 @Override
	 public String postUnsatisfaisableClasses(String filePath, String Url) throws Exception {
	     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	     OWLOntology ontology = null ;
		        if (filePath == null && Url.isEmpty() == false && (Url.startsWith("http") || Url.startsWith("ftp"))) {
		        	
		        	ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
		        } else if(filePath.isEmpty() == false && Url == null) {
		        	
		            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
		        } else {
		        	return null;
		        }

	        PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
	        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

	        reasonerUnsatisfaisability myData = new reasonerUnsatisfaisability();
	        Node<OWLClass> unsatisfiableClasses = reasoner.getUnsatisfiableClasses();

	        OWLClass[] unsatisfiable = new OWLClass[unsatisfiableClasses.getSize()];
	        int i = 0;
	        if (unsatisfiableClasses.getSize() > 0) {
	            for (OWLClass cls : unsatisfiableClasses) {
	                unsatisfiable[i] = cls;
	                i++;
	            }
	            myData.setUnsatisfaisable(unsatisfiable);
	        }

	        OWLClass[] unsatisfiableArray = myData.getUnsatisfaisable();
	        String[] iriStrings = new String[unsatisfiableArray.length];
	        for (int j = 0; j < unsatisfiableArray.length; j++) {
	            iriStrings[j] = unsatisfiableArray[j].toStringID();
	        }
	        JSONObject jsonObject = new JSONObject();
	        jsonObject.put("unsatisfiable", iriStrings);
	        String jsonString = jsonObject.toString();
	        return jsonString;
	    }
	 
	 @Override
	 public String postConsistency(String filePath, String Url) throws OWLOntologyCreationException, JsonProcessingException,Exception {
	     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	     OWLOntology ontology = null ;
	        if (filePath == null && Url.isEmpty() == false && (Url.startsWith("http") || Url.startsWith("ftp"))) {
	        	
	        	ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
	        } else if(filePath.isEmpty() == false && Url == null) {
	        	
	            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
	        } else {
	        	return null;
	        }
	         PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
	         OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
	         reasonerConsistency myData = new reasonerConsistency();
	         boolean consistency = reasoner.isConsistent();
	         System.out.println(consistency);
	         myData.setConsistency(consistency);
	         JSONObject jsonObject = new JSONObject();
	         jsonObject.put("consistency", myData.getConsistency());
	         String jsonString = jsonObject.toString();
	         return jsonString;
	         
	         
	 	}
	 
	 
	 // END
		 @Override
		 public String getConsistency(String filePath, String Url) throws OWLOntologyCreationException, JsonProcessingException,Exception {
		     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		     OWLOntology ontology = null ;
		        if (filePath == null && Url.isEmpty() == false && (Url.startsWith("http") || Url.startsWith("ftp"))) {
		        	
		        	ontology = manager.loadOntologyFromOntologyDocument(IRI.create(Url));
		        } else if(filePath.isEmpty() == false && Url == null) {
		        	
		            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
		        } else {
		        	return null;
		        }
		         PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
		         OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		         reasonerConsistency myData = new reasonerConsistency();
		         boolean consistency = reasoner.isConsistent();
		         System.out.println(consistency);
		         myData.setConsistency(consistency);
		         JSONObject jsonObject = new JSONObject();
		         jsonObject.put("consistency", myData.getConsistency());
		         String jsonString = jsonObject.toString();
		         return jsonString;
		         
		         
		 	}
		 @Override
		 public String getInferences(String filePath, String url) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException, Exception {
		     OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		     OWLOntology ontology = null ;
		        if (filePath == null && url.isEmpty() == false && (url.startsWith("http") || url.startsWith("ftp"))) {
		        	
		        	ontology = manager.loadOntologyFromOntologyDocument(IRI.create(url));
		        } else if(filePath.isEmpty() == false && url == null) {
		        	
		            ontology = manager.loadOntologyFromOntologyDocument(new File(filePath));
		        } else {
		        	return null;
		        }
		        PelletReasonerFactory reasonerFactory = new PelletReasonerFactory();
		        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		        String fileName = "inferred-ontology.owl";
		        reasoner.precomputeInferences(InferenceType.values());
		        InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner);
		        OWLOntology inferredOntology = manager.createOntology();
		        OWLDataFactory dataFactory = manager.getOWLDataFactory();
		        iog.fillOntology(dataFactory, inferredOntology);

		        Resource resource = new FileSystemResource(fileName);
		        if (resource instanceof WritableResource) {
		            try (OutputStream outputStream =((WritableResource) resource).getOutputStream()) {
		            manager.saveOntology(inferredOntology, IRI.create(resource.getURI()));
		            System.out.println("New file created: " + fileName);
		        } catch (IOException e) {
		            System.out.println("An error occurred: " + e.getMessage());
		            e.printStackTrace();
		        }
		        } 
		        // Load the ontology from the file
		        ontology = manager.loadOntologyFromOntologyDocument(resource.getFile());
		        StringBuilder sb = new StringBuilder();
		        // Add the inferred axioms to the list
		        for (OWLAxiom axiom : inferredOntology.getAxioms()) {
		        	sb.append(axiom.toString());
		            
		        }
		        String axiomsString = sb.toString();
		        JSONObject jsonObject = new JSONObject();
		        jsonObject.put("inference", axiomsString);
		         String jsonString = jsonObject.toString();
		         return jsonString;
		    }
		 
		 
		    public static class SameIndividualAxiomGenerator extends InferredIndividualAxiomGenerator<OWLSameIndividualAxiom> {

		        @Override
		        protected void addAxioms(OWLNamedIndividual entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLSameIndividualAxiom> result) {
		            for (OWLNamedIndividual i : reasoner.getSameIndividuals(entity).getEntities()) {
		                if (!entity.equals(i)) {
		                    result.add(dataFactory.getOWLSameIndividualAxiom(entity, i));
		                }
		            }
		        }

				@Override
				public String getLabel() {
					// TODO Auto-generated method stub
					return "Same individual axioms";
				}
		    }
		    
		    public class InferredEquivalentClassesAxiomGenerator extends InferredClassAxiomGenerator<OWLEquivalentClassesAxiom> {

		        @Override
		        protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLEquivalentClassesAxiom> result) {
		            Set<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(entity).getEntities();
		            if (equivalentClasses.size() > 1) {
		                result.add(dataFactory.getOWLEquivalentClassesAxiom(equivalentClasses));
		            }
		        }

		        @Override
		        public String getLabel() {
		            return "Equivalent classes";
		        }
		    }
		    
		    public class InferredDifferentIndividualAxiomGenerator extends InferredIndividualAxiomGenerator<OWLDifferentIndividualsAxiom> {

		        @Override
		        protected void addAxioms(OWLNamedIndividual entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLDifferentIndividualsAxiom> result) {
		        	Set<OWLNamedIndividual> differentIndividuals = reasoner.getDifferentIndividuals(entity).getFlattened();
		            if (!differentIndividuals.isEmpty()) {
		            	result.add(dataFactory.getOWLDifferentIndividualsAxiom(Stream.concat(Stream.of(entity), differentIndividuals.stream()).toArray(OWLIndividual[]::new)));

		            }
		        }

		        @Override
		        public String getLabel() {
		            return "Different individuals";
		        }
		    }
		    
		    public class InferredIntersectionOfAxiomGenerator extends InferredClassAxiomGenerator<OWLEquivalentClassesAxiom> {

		        @Override
		        protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLEquivalentClassesAxiom> result) {
		            NodeSet<OWLClass> directSuperClasses = reasoner.getSuperClasses(entity, true);
		            if (directSuperClasses.isEmpty()) {
		                return;
		            }

		            Set<OWLClassExpression> operands = new HashSet<>();
		            for (Node<OWLClass> superClassNode : directSuperClasses.getNodes()) {
		                operands.add(superClassNode.getRepresentativeElement());
		            }

		            if (operands.size() > 1) {
		                OWLObjectIntersectionOf intersection = dataFactory.getOWLObjectIntersectionOf(operands);
		                OWLEquivalentClassesAxiom axiom = dataFactory.getOWLEquivalentClassesAxiom(entity, intersection);
		                result.add(axiom);
		            }
		        }

		        @Override
		        public String getLabel() {
		            return "Inferred Intersection Of";
		        }
		    }
		    
		    public class InferredUnionOfAxiomGenerator extends InferredClassAxiomGenerator<OWLSubClassOfAxiom> {
		        @Override
		        protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLSubClassOfAxiom> result) {
		            Set<OWLClass> directSuperclasses = reasoner.getSuperClasses(entity, true).getFlattened();
		            if (directSuperclasses.size() > 1) {
		                result.add(dataFactory.getOWLSubClassOfAxiom(entity, dataFactory.getOWLObjectUnionOf(directSuperclasses)));
		            }
		        }

		        @Override
		        public String getLabel() {
		            return "Inferred Union Of";
		        }
		    }
		    

		    public class InferredDisjointClassesAxiomGenerator extends InferredClassAxiomGenerator<OWLDisjointClassesAxiom> {

		        @Override
		        protected void addAxioms(OWLClass entity, OWLReasoner reasoner, OWLDataFactory dataFactory, Set<OWLDisjointClassesAxiom> result) {
		            Set<OWLClass> allClasses = reasoner.getRootOntology().getClassesInSignature();
		            for (OWLClass cls : allClasses) {
		                if (!cls.equals(entity)) {
		                	System.out.println(entity);
		                	Set<OWLClass> theEQ = reasoner.getEquivalentClasses(entity).getEntities();
		                	System.out.println("COME ONNNNNN "+theEQ);
		                    NodeSet<OWLClass> disjointClasses = reasoner.getDisjointClasses(entity);
		                    System.out.println(disjointClasses);
		                    if (disjointClasses.containsEntity(cls)) {
		                    	
		                        Set<OWLClass> equivalentClasses1 = reasoner.getEquivalentClasses(entity).getEntities();
		                        Set<OWLClass> equivalentClasses2 = reasoner.getEquivalentClasses(cls).getEntities();
		                        for (OWLClass eqClass1 : equivalentClasses1) {
		                            for (OWLClass eqClass2 : equivalentClasses2) {
		                                if (!eqClass1.equals(eqClass2)) {
		                                    result.add(dataFactory.getOWLDisjointClassesAxiom(eqClass1, eqClass2));
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }

		        @Override
		        public String getLabel() {
		            return "Disjoint classes";
		        }
		    }
}
//		        System.out.println("Ontology ComputeInference Completed");
//		        Set<OWLAxiom> axioms = ontology.getAxioms();
//		        List<reasonerExtractTriples> triplesList = new ArrayList<>(); 
//		        // Iterate through axioms
//		        for (OWLAxiom axiom : axioms) {
//		            String subject = null;
//		            String predicate = null;
//		            String object = null;
//		            
//		            // Extract subject and object
//		            if (axiom instanceof OWLSubClassOfAxiom) {
//		                OWLSubClassOfAxiom subClassAxiom = (OWLSubClassOfAxiom) axiom;
//		                subject = subClassAxiom.getSubClass().toString();
//		                object = subClassAxiom.getSuperClass().toString();
//		                predicate = "subClassOf";
//		                if (subject != null && predicate != null && object !=null) {
//		                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                }
//		            } else if (axiom instanceof OWLEquivalentClassesAxiom) {
//		                OWLEquivalentClassesAxiom equivClassesAxiom = (OWLEquivalentClassesAxiom) axiom;
//		                Set<OWLClassExpression> classExpressions = equivClassesAxiom.getClassExpressions();
//		                for (OWLClassExpression classExpression : classExpressions) {
//		                    if (!classExpression.isAnonymous()) {
//		                        if (subject == null) {
//		                            subject = classExpression.asOWLClass().toStringID();
//		                            predicate = "equivalentTo";
//		                        } else {
//		                            object = classExpression.asOWLClass().toStringID();
//		                        }
//		                        if (subject != null && predicate != null && object !=null) {
//		                            triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                            }
//		    	                
//		                    }
//		                }
//		            } else if (axiom instanceof OWLClassAssertionAxiom) {
//		                OWLClassAssertionAxiom classAssertionAxiom = (OWLClassAssertionAxiom) axiom;
//		                subject = classAssertionAxiom.getIndividual().toStringID();
//		                object = classAssertionAxiom.getClassExpression().toString();
//		                predicate = "type";
//		                if (subject != null && predicate != null && object !=null) {
//		                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                }
//		            } else if (axiom instanceof OWLObjectPropertyAssertionAxiom) {
//		                OWLObjectPropertyAssertionAxiom objectPropertyAssertionAxiom = (OWLObjectPropertyAssertionAxiom) axiom;
//		                subject = objectPropertyAssertionAxiom.getSubject().toStringID();
//		                object = objectPropertyAssertionAxiom.getObject().toStringID();
//		                predicate = objectPropertyAssertionAxiom.getProperty().toString();
//		                if (subject != null && predicate != null && object !=null) {
//		                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                }
//		            } else if (axiom instanceof OWLDataPropertyAssertionAxiom) {
//		                OWLDataPropertyAssertionAxiom dataPropertyAssertionAxiom = (OWLDataPropertyAssertionAxiom) axiom;
//		                subject = dataPropertyAssertionAxiom.getSubject().toStringID();
//		                object = dataPropertyAssertionAxiom.getObject().toString();
//		                predicate = dataPropertyAssertionAxiom.getProperty().toString();
//		                if (subject != null && predicate != null && object !=null) {
//		                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                }
//		            } else if (axiom instanceof OWLAnnotationAssertionAxiom) {
//		                OWLAnnotationAssertionAxiom annotationAssertionAxiom = (OWLAnnotationAssertionAxiom) axiom;
//		                subject = annotationAssertionAxiom.getSubject().toString();
//		                object = annotationAssertionAxiom.getValue().toString();
//		                predicate = annotationAssertionAxiom.getProperty().toString();
//		                if (subject != null && predicate != null && object !=null) {
//		                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                }
//		            } else if (axiom instanceof OWLObjectPropertyDomainAxiom) {
//		                OWLObjectPropertyDomainAxiom objectPropertyDomainAxiom = (OWLObjectPropertyDomainAxiom) axiom;
//		                subject = objectPropertyDomainAxiom.getProperty().toString();
//		                object = objectPropertyDomainAxiom.getDomain().toString();
//		                predicate = "domain";
//		                if (subject != null && predicate != null && object !=null) {
//		                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                }
//		            } else if (axiom instanceof OWLObjectPropertyRangeAxiom) {
//		                OWLObjectPropertyRangeAxiom objectPropertyRangeAxiom = (OWLObjectPropertyRangeAxiom) axiom;
//		                subject = objectPropertyRangeAxiom.getProperty().toString();
//		                object = objectPropertyRangeAxiom.getRange().toString();
//		                predicate = "range";
//		                if (subject != null && predicate != null && object !=null) {
//		                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                }
//		            } else if (axiom instanceof OWLFunctionalObjectPropertyAxiom) {
//		                OWLFunctionalObjectPropertyAxiom functionalObjectPropertyAxiom = (OWLFunctionalObjectPropertyAxiom) axiom;
//		                subject = functionalObjectPropertyAxiom.getProperty().toString();
//		                predicate = "functionalProperty";
//		                
//		                if (subject != null && predicate != null) {
//		                triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                }
//		            } else if (axiom instanceof OWLDeclarationAxiom) {
//		                OWLDeclarationAxiom declarationAxiom = (OWLDeclarationAxiom) axiom;
//		                if (declarationAxiom.getEntity() instanceof OWLClass) {
//		                    subject = declarationAxiom.getEntity().toStringID();
//		                    predicate = "classDeclaration";
//		                    if (subject != null && predicate != null) {
//		                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                    }
//		                } else if (declarationAxiom.getEntity() instanceof OWLObjectProperty) {
//		                    subject = declarationAxiom.getEntity().toStringID();
//		                    predicate = "objectPropertyDeclaration";
//		                    if (subject != null && predicate != null) {
//		                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                    }
//		                } else if (declarationAxiom.getEntity() instanceof OWLDataProperty) {
//		                    subject = declarationAxiom.getEntity().toStringID();
//		                    predicate = "dataPropertyDeclaration";
//		                    if (subject != null && predicate != null) {
//		                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                    }
//		                } else if (declarationAxiom.getEntity() instanceof OWLNamedIndividual) {
//		                    subject = declarationAxiom.getEntity().toStringID();
//		                    predicate = "namedIndividualDeclaration";
//		                    if (subject != null && predicate != null) {
//		                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                    }
//		                }else if (declarationAxiom.getEntity() instanceof OWLDatatype) {
//		                    subject = declarationAxiom.getEntity().toStringID();
//		                    predicate = "datatypeDeclaration";
//		                    if (subject != null && predicate != null) {
//		                    triplesList.add(new reasonerExtractTriples(subject, predicate, object));
//		                    }
//		                }
//		            }
//
//		            
//		        }
//		        return triplesList;
//		         
//		 	}
//				return null;
//		 }





