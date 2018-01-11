package com.funny.vo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义的一个JSON类,简化操作
 */
public class JSON {

	private JSONObject obj = null;

	/**
	 * 构造函数，接受一个JSON字符串，格式错误则创建个空JSON
	 */
	public JSON() {
		this("{}");
	}

	/**
	 * 构造函数，接受一个JSONObject对象,为null则创建个空JSON
	 */
	public JSON(JSONObject json) {
		this.obj = json;
		if (this.obj == null) {
			obj = JSONObject.fromObject("{'error':'json为空'}");
		}
	}

	public JSON(Object json) {
		if (JSONUtils.mayBeJSON(json + "")) {
			obj = JSONObject.fromObject(json + "");
		} else {
			obj = JSONObject.fromObject("{'error':'json格式错误: " + json + "'}");
		}
	}

	/**
	 * 相当于查询名字等于张三的人的衣服的颜色 在一个JSON数组中比如 
	 * 
	 *	{
	 *	  "testArr": [
	 *	    {
	 *	      "code": "AA1", 
	 *	      "name": {
	 *	        "content": "阿塔斯卡德罗，加利福尼亚州"
	 *	      }
	 *	    }, 
	 *	    {
	 *	      "code": "AAC", 
	 *	      "name": {
	 *	        "content": "亚琛"
	 *	      }
	 *	    }
	 *	  ]
	 *	}
	 * 
	 * 查找code=AAC的JSON的name/content的内容，那么
	 * 
	 * arrKey: testArr codeVal: AAC codeLoc: code conetentLoc: name/content
	 */
	public String getCodeContent(String arrKey, String codeLoc, String codeVal, String conetentLoc) {
		for (JSON json : this.getList(arrKey)) {
			if (json.isTrue(codeLoc, codeVal)) {
				return json.getStr(conetentLoc);
			}
		}
		return "";
	}

	public JSON getJSON(String arrKey, String codeVal, String codeLoc) {
		for (JSON json : this.getList(arrKey)) {
			if (json.isTrue(codeLoc, codeVal)) {
				return json;
			}
		}
		return null;
	}

	/**
	 * 设置节点值 man/age,18 ==> {man:{age:18}} man[1]/age,18 ==> {man[{},{age:18}]}
	 */
	public void put(String key, Object value) {
		if (key == null || key == "" || value == null || value == "") {
			return;
		}

		JSONObject o = obj;

		/*
		 * 从顶层开始遍历下去直到倒數第二層，不存在则新增节点 man/age 遍历到man层，不存在则创建元素 man[2]/age
		 * 遍历到man层，不存在则创建1个man数组，里面有3个空的{}
		 */

		String[] keyArr = key.split("/");
		for (int i = 0; i < keyArr.length - 1; i++) {
			key = keyArr[i];
			if (key.contains("[") && key.contains("]")) { // "man[2]"
				int index = Integer.parseInt(key.substring(key.indexOf("[") + 1, key.indexOf("]")));
				key = key.substring(0, key.indexOf("["));

				if (o.get(key) == null) {
					StringBuffer objSB = new StringBuffer("[");
					for (int j = 0; j <= index; j++) {
						objSB.append("{},");
					}
					if (objSB.length() > 0) {
						objSB.deleteCharAt(objSB.length() - 1);
					}
					objSB.append("]");
					o.element(key, objSB.toString());
				}
				for (int k = o.getJSONArray(key).size(); k <= index; k++) {
					o.getJSONArray(key).add("{}");
				}

				o = o.getJSONArray(key).getJSONObject(index);
			} else { // "man"
				if (o.get(key) == null) {
					o.put(key, "{}");
				}
				o = o.getJSONObject(key);
			}
		}

		/*
		 * 到达最低层，开始赋值 1. age:18 直接创建元素age并赋值18 2. age[0]:18 创建一个JSON数组然后在指定的位置赋值
		 */
		key = keyArr[keyArr.length - 1];
		if (key.contains("[") && key.contains("]")) { // "man[2]"
			int index = Integer.parseInt(key.substring(key.indexOf("[") + 1, key.indexOf("]")));
			key = key.substring(0, key.indexOf("["));
			if (o.get(key) == null) {
				o.element(key, "[]");
			}
			for (int i = o.getJSONArray(key).size(); i < index; i++) {
				o.getJSONArray(key).add(null);
			}
			o.getJSONArray(key).add(value);
		} else { // "man"
			o.put(key, value);
		}
	}

	/**
	 * 判断此节点下的值是否与value相同
	 */
	public boolean isTrue(String path, String value) {
		return getStr(path).equals(value);
	}

	/**
	 * 获取字符串
	 */
	public String getStr(String path) {
		return getJSON(path, "");
	}

	public JSON getJSON(String path) {
		return new JSON(getJSON(path, new JSONObject()));
	}

	/**
	 * 获取JSONObject对象
	 */
	public JSONObject getJSONObj(String path) {
		return getJSON(path, new JSONObject());
	}

	public List<JSON> getList(String path) {
		List<JSON> list = new ArrayList<JSON>();
		JSONArray arr = getJSON(path, new JSONArray());
		for (int i = 0; i < arr.size(); i++) {
			list.add(new JSON(arr.get(i)));
		}
		return list;
	}

	/**
	 * 获取JSONArray对象
	 */
	public JSONArray getArr(String path) {
		return getJSON(path, new JSONArray());
	}

	/**
	 * 转换成字符串
	 */
	public String toString() {
		return obj.toString();
	}

	/**
	 * 获取JSON里面数据
	 * 
	 * @param json
	 *            JSON对象
	 * 
	 * @param matches
	 *            数组所在的位置, 比如 "user/id"
	 * 
	 * @param t
	 *            返回的数据格式, 支持new JSONObject(), new JSONArray(), "" 3种格式
	 */
	@SuppressWarnings("unchecked")

	public <T> T getJSON(String path, T t) {
		/*
		 * 不符合条件的刷下来
		 */
		String type = t.getClass().toString();
		if (!(type.equals(JSONObject.class.toString())
				|| type.equals(JSONArray.class.toString())
				|| type.equals(String.class.toString())
				|| type.equals(Integer.class.toString()))
				|| this.obj == null
				|| path == null) {
			return t;
		}
		String[] matchesArr = path.split("/");
		Object obj = this.obj;
		try {
			/*
			 * 提取
			 */
			for (int i = 0; i < matchesArr.length; i++) {
				if (matchesArr[i].contains("[") && matchesArr[i].contains("]")) {// JSONArray
					int index = Integer.parseInt(matchesArr[i].substring(matchesArr[i].indexOf("[") + 1, matchesArr[i].indexOf("]")));
					obj = ((JSONObject) obj).getJSONArray(matchesArr[i].substring(0, matchesArr[i].indexOf("["))).get(index);
				} else {
					obj = ((JSONObject) obj).get(matchesArr[i]);
				}
			}
			/*
			 * 验证
			 */
			if (type.equals((String.class.toString()))) {
				obj = obj.toString();
				obj = "null".equals(obj) ? "" : obj;
			} else if (type.equals(JSONArray.class.toString())) {
				((JSONArray) obj).toString();
			} else if (type.equals(JSONObject.class.toString())) {
				((JSONObject) obj).toString();
			}
		} catch (Exception e) {
			return t;
		}
		return (T) obj;
	}

	public JSONObject getObj() {
		return obj;
	}

	public void setObj(JSONObject obj) {
		this.obj = obj;
	}
}
