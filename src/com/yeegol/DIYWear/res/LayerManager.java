package com.yeegol.DIYWear.res;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Created by intel-ivy on 13-11-6.
 */
public class LayerManager {

	public LayerManager() {
		if (itemTable == null) {
			itemTable = new Hashtable<String, Item>();
			itemTable.put("0", new Item("0", "TATTOO", "纹身", 0, "0000000000"));
			itemTable.put("1", new Item("1", "MAKE_UP", "彩妆", 0, "9000000000"));
			itemTable.put("2", new Item("2", "BAR", "文胸", 2, "0009900000"));
			itemTable.put("3", new Item("3", "BRIEFS", "底裤", 2, "0000099000"));
			itemTable.put("4", new Item("4", "LEOTARDS", "连体内衣", 2,
					"0009999000"));
			itemTable
					.put("5", new Item("5", "TIGHTS", "紧身裤袜", 3, "0000099999"));
			itemTable.put("6", new Item("6", "SOCKS", "袜子", 4, "0000000099"));
			itemTable.put("7",
					new Item("7", "LEGGINGS", "打底裤", 6, "0000099999"));
			itemTable.put("8", new Item("8", "EARRING", "首饰(耳环))", 6,
					"9000000000"));
			itemTable.put("9", new Item("9", "FALL_CLOTHING_UP", "秋衣", 8,
					"0009900000"));
			itemTable.put("10", new Item("10", "LITTLE_SWEATER", "汗衫", 8,
					"0009900000"));
			itemTable.put("11", new Item("11", "FALL_CLOTHING_DOWN", "秋裤", 8,
					"0000099000"));
			itemTable.put("12", new Item("12", "BANGLE", "首饰(手臂))", 9,
					"0000900000"));
			itemTable.put("13", new Item("13", "ANKLE_RING", "首饰(脚踝))", 9,
					"0000000009"));
			itemTable.put("14", new Item("14", "SHOE_HEIGHT", "鞋子(高帮))", 10,
					"0000000099"));
			itemTable.put("15", new Item("15", "SHOE_MID", "鞋子(中帮))", 10,
					"0000000099"));
			itemTable.put("16", new Item("16", "SHOE_LOW", "鞋子(低帮))", 10,
					"0000000009"));
			itemTable.put("17", new Item("17", "TROUSERS_HEIGHT", "裤子(长裤))",
					10, "0000099990"));
			itemTable.put("18", new Item("18", "TROUSERS_MID", "裤子(中裤))", 10,
					"0000099900"));
			itemTable.put("19", new Item("19", "TROUSERS_LOW", "裤子(短裤))", 10,
					"0000099000"));
			itemTable.put("20", new Item("20", "T_SHIRT", "T恤", 10,
					"0004400000"));
			itemTable.put("21",
					new Item("21", "BLOUSE", "衬衣", 10, "0004400000"));
			itemTable.put("22", new Item("22", "T_SHIRT_L", "长袖T恤", 10,
					"0004400000"));
			itemTable
					.put("23", new Item("23", "SKIRT", "短裙", 10, "0000044000"));
			itemTable
					.put("24", new Item("24", "DRESS", "长裙", 10, "0000044000"));
			itemTable.put("25",
					new Item("25", "GLOVES", "手套", 12, "0000900000"));
			itemTable.put("26", new Item("26", "TIE", "领带", 12, "0900000000"));
			itemTable.put("27",
					new Item("27", "BLOUSE", "马甲", 13, "0009900000"));
			itemTable.put("28", new Item("28", "SWEATER_IN", "毛衣(内穿))", 13,
					"0009900000"));
			itemTable.put("29", new Item("29", "ROBE", "礼服（女）", 13,
					"000999900000099990000009999000"));
			itemTable.put("30", new Item("30", "BELT", "腰带", 13, "0000090000"));
			itemTable.put("31", new Item("31", "SWEATER_OUT", "毛衣（外）", 13,
					"0009900000"));
			itemTable.put("32",
					new Item("32", "JACKET", "外套", 13, "0009900000"));
			itemTable.put("33", new Item("33", "NECKLACE", "首饰(项链))", 15,
					"0090000000"));
			itemTable.put("34", new Item("34", "BROOCH", "首饰(胸花))", 15,
					"0009000000"));
			itemTable.put("35", new Item("35", "BAG", "包", 15, "0009900000"));
			itemTable.put("36", new Item("36", "GLASSES", "眼镜", 15,
					"9000000000"));
			itemTable
					.put("37", new Item("37", "SCARF", "围巾", 16, "0009900000"));
			itemTable.put("38", new Item("38", "HAIR", "发型", 16, "9000000000"));
			itemTable.put("39", new Item("39", "HAIRPIN", "首饰(头饰))", 18,
					"9000000000"));
			itemTable.put("40", new Item("40", "HAT", "帽子", 18, "9000000000"));
		}
	}

	static LayerManager instance;

	public static LayerManager getInstance() {
		if (instance == null) {
			instance = new LayerManager();
		}
		return instance;
	}

	// / record the every item in Loop
	class ItemInfo extends Item {
		public String id;

		ItemInfo(String type, String enName, String cnName, int layer,
				String space, String id) {
			super(type, enName, cnName, layer, space);
			this.id = id;
		}
	}

	// / record table data
	class Item {
		Item(String type, String enName, String cnName, int layer, String space) {
			this.type = type;
			this.enName = enName;
			this.cnName = cnName;
			this.layer = layer;
			this.space = space;
		}

		public String type;
		public String enName;
		public String cnName;
		public int layer;
		public String space;
	}

	private static int MaxLayerNum = 12;
	private static Dictionary<String, Item> itemTable;

	public Vector<String> includeItem(Vector<String> Ids,
			Vector<Integer> Types, String id, int type) {
		Ids.add(id);
		Types.add(type);

		LinkedList<ItemInfo> linkedList = getItemInfo(Ids, Types);
		ItemInfo itm = linkedList.pop();

		LinkedList<ItemInfo> Wearing = new LinkedList<ItemInfo>();
		Wearing.add(itm);

		// loop
		int i = linkedList.size() - 1;
		for (; i >= 0; i--) {
			if (linkedList.get(i).id == itm.id) {
				linkedList.remove(i);
				break;
			}
		}
		if (i == -1) {
			while (linkedList.size() > 0) {
				ItemInfo putOn = linkedList.pop();
				for (i = 0; i < Wearing.size(); i++) {
					if (Wearing.get(i).layer > putOn.layer)
						break;
				}
				Wearing.remove(i);
				if (!isLegalState(Wearing))
					Wearing.remove(i);
			}
		} else
			Wearing = linkedList;

		Vector<String> R = new Vector<String>();
		while (Wearing.size() > 0)
			R.add(Wearing.pop().id);
		return R;
	}

	public Vector<Integer> canReorder(Vector<String> Ids,
			Vector<Integer> Types, String id, int type) {
		Ids.add(id);
		Types.add(type);
		LinkedList<ItemInfo> I = getItemInfo(Ids, Types);
		ItemInfo itm = I.pop();
		Vector<Integer> R = new Vector<Integer>();
		for (int i = 0; i < I.size(); i++) {
			if (I.get(i).layer == itm.layer)
				R.add(i);
		}
		return R;
	}

	private boolean isLegalState(LinkedList<ItemInfo> wearing) {
		int i = 0;
		int j = 0;
		int length;
		for (i = wearing.size(); i >= 0; i--) {
			for (j = i - 1; j >= 0; j--) {
				if (wearing.get(i).type == wearing.get(j).type)
					return false;
			}
		}

		Hashtable<Integer, Integer> ht = new Hashtable<Integer, Integer>();
		for (i = wearing.size() - 1; i >= 0; i--) {
			ItemInfo itm = wearing.get(i);
			String s = itm.space;
			length = s.length();
			for (j = 0; j < length; j++) {
				int idx = (itm.layer + 1) * 10 - j;
				if (ht.containsKey(idx))
					ht.put(idx, 0);
				// ht.get(idx) += Integer.parseInt(s,j);
				int a = ht.get(idx);
				a += Integer.valueOf(s.charAt(j)).intValue();
				ht.put(idx, a);
				if (ht.get(idx) > 9)
					return false;
			}
		}
		return true;
	}

	private LinkedList<ItemInfo> getItemInfo(Vector<String> Ids,
			Vector<Integer> Types) {

		LinkedList<ItemInfo> R = new LinkedList<ItemInfo>();
		for (int i = 0; i < Types.size(); i++) {
			Item item = itemTable.get(Types.get(i));
			R.add(new ItemInfo(item.type, item.enName, item.cnName, item.layer,
					item.space, Ids.get(i)));
		}
		return R;
	}// endfunction
}
