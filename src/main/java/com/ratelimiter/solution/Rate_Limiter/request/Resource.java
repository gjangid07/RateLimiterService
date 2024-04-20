package com.ratelimiter.solution.Rate_Limiter.request;

/**
 * @author gjangid
 *
 */
public class Resource {

	private String urlString;

	public Resource(String urlString) {
		super();
		this.urlString = urlString;
	}

	public String getUrlString() {
		return urlString;
	}

	public void setUrlString(String urlString) {
		this.urlString = urlString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((urlString == null) ? 0 : urlString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resource other = (Resource) obj;
		if (urlString == null) {
			if (other.urlString != null)
				return false;
		} else if (!urlString.equals(other.urlString))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Resource [urlString=" + urlString + "]";
	}

}
