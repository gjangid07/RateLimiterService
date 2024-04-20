package com.ratelimiter.solution.Rate_Limiter.api;

import com.ratelimiter.solution.Rate_Limiter.request.Resource;
import com.ratelimiter.solution.Rate_Limiter.request.User;

/**
 * @author gjangid
 *
 */
// User+API pair is considered as Rate Limiting parameter. This class Object
// created using information out of the input requests and later processed for
// Rate Limit.
public class RateLimitParam {

	private User user;
	private Resource resource;

	public RateLimitParam(User user, Resource resource) {
		super();
		this.user = user;
		this.resource = resource;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		RateLimitParam other = (RateLimitParam) obj;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

}
