package mlconsulta;


public class Articulo {

	private String id;
	private String title;
	private String permalink;

	public Articulo(String id, String title, String permalink) {
		this.id = id;
		this.title = title;
		this.permalink = permalink;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public String getPermalink() {
		return permalink;
	}

}
