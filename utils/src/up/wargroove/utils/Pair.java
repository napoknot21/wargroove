package up.wargroove.utils;

public class Pair<T, U> {

	public T first;
	public U second;

	public Pair() {}

	public Pair(T first, U second) {

		this.first = first;
		this.second = second;

	}

	public void swap(Pair<T, U> copy) {

		first = copy.first;
		second = copy.second;

	}

	public boolean equals(Object object) {

		if(object.getClass() != Pair.class) return false;

		Pair<T, U> pair = (Pair<T, U>) object;
		return first.equals(pair.first) && second.equals(pair.second);

	}

	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}

}
