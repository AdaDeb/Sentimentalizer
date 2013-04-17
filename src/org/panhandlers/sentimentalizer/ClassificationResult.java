package org.panhandlers.sentimentalizer;

public class ClassificationResult implements Comparable<ClassificationResult> {
	private double p;
	private String category;

	public ClassificationResult() {
	}

	public ClassificationResult(String category, double p) {
		this.category = category;
		this.p = p;
	}

	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public int compareTo(ClassificationResult o) {
		if (this.equals(o)) {
			return 0;
		}
		if (o.getP() > this.getP()) {
			return -1;
		}
		if (o.getP() < this.getP()) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
		result = prime * result + (int) Double.doubleToLongBits(p);
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
		ClassificationResult other = (ClassificationResult) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (Double.doubleToLongBits(p) != Double.doubleToLongBits(other.p))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return category + " with probability " + p;
	}

}
