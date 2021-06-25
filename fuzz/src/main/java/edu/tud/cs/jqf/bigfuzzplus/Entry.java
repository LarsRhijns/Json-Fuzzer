package edu.tud.cs.jqf.bigfuzzplus;

class Entry<A, B> {
	A key;
	B value;

	Entry(A key, B value) {
		this.key = key;
		this.value = value;
	}

	public A getKey() {
		return key;
	}

	public B getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "" + key + "=" + value;
	}
}
