package jscope.configuration;



public class Scope {

    private String description;

    private String id;

    private Status status;


    public String getDescription() {
        return this.description;
    }


    public void setDescription(final String description) {
        this.description = description;
    }


    public String getId() {
        return this.id;
    }


    public void setId(final String id) {
        this.id = id;
    }


    public Status getStatus() {
        return this.status;
    }


    public void setStatus(final Status status) {
        this.status = status;
    }

    /**
     * Setter utilisé par maven/plexus.
     * @param value
     */
    public void setStatus(final String value) {
        this.status = Status.valueOf(value);
    }


    @Override
    public String toString() {
        return this.id + ":" + this.status;
    }


}
