package com.example.frolic;

/**
 * Model class to represent image data in the Firebase database.
 * Includes information about the image URL, its type, and the associated collection/document ID.
 */
public class ImageData {
    private String url;
    private String type; // Entrant Profile or Event Poster
    private String documentId;
    private String collection;

    // Default constructor (required for Firestore's toObject method)
    public ImageData() {}

    // Constructor with parameters
    public ImageData(String url, String type, String documentId, String collection) {
        this.url = url;
        this.type = type;
        this.documentId = documentId;
        this.collection = collection;
    }

    // Getter and Setter for URL
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // Getter and Setter for Type
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Getter and Setter for Document ID
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    // Getter and Setter for Collection
    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }
}

