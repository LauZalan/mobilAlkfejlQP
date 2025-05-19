package com.example.quickprint;

public class Document {

    private String Name;
    private String Owner;

    private String Uri;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public String getUri() {
        return Uri;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public Document(String name, String uri, String owner) {
        this.Name = name;
        this.Uri = uri;
        this.Owner = owner;
    }

    public Document(String name) {
        this.Name = name;
    }
}
