package com.lxsx.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.lxsx.gulimall.valid.AddGroup;
import com.lxsx.gulimall.valid.ListValid;
import com.lxsx.gulimall.valid.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author lxsx
 * @email lixiang.qinbo@gmail.com
 * @date 2022-09-12 22:37:56
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "修改时不能brandId为空",groups = {UpdateGroup.class})
	@Null(message = "新增时brandId必须为空",groups ={AddGroup.class })
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
//	@NotNull(message = "品牌名不能为空！")
	@NotBlank(message = "品牌名字不能为null或者空",
	groups = {AddGroup.class,UpdateGroup.class}) //,无论增还是改 都需要满足至少有一个为非”“的字符
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(message = "品牌地址不能为空 ",
			groups = {AddGroup.class,UpdateGroup.class})
	@URL(message = "品牌地址必须是url",
			groups = {AddGroup.class,UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	@NotNull(message = "描述不能为null！",
	groups = {AddGroup.class,UpdateGroup.class})
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	//自定义校验器


	@ListValid(value = {0,1},message = "showStatus只能是0或者1",
			groups ={AddGroup.class,UpdateGroup.class} )
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	//todo 正则有问题
//	@Pattern(regexp ="/^[a-zA-Z]$/",message = "检索的字母必须是英文字母",
//			groups = {AddGroup.class,UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@Min(value = 0,message = "排序必须大于0",
			groups = {AddGroup.class,UpdateGroup.class})
	private Integer sort;

}
