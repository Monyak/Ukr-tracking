package reedey.shared.tracking.entity;

import java.io.Serializable;
import java.util.Arrays;

public class TrackingItem implements Serializable {

	private static final long serialVersionUID = -7143422317502796284L;

	private String barCode;
	private String name;
	private HistoryItem[] items;

	public TrackingItem() {

	}

	public TrackingItem(String barCode, String name, HistoryItem[] items) {
		this.barCode = barCode;
		this.name = name;
		this.items = items;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HistoryItem[] getItems() {
		return items;
	}

	public void setItems(HistoryItem[] items) {
		this.items = items;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((barCode == null) ? 0 : barCode.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		TrackingItem other = (TrackingItem) obj;
		if (barCode == null) {
			if (other.barCode != null)
				return false;
		} else if (!barCode.equals(other.barCode))
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TrackingItem [barCode=" + barCode + ", name=" + name
				+ ", items=" + Arrays.toString(items) + "]";
	}
}
