package com.rocket.myshop.dto.validattion;

import javax.validation.GroupSequence;


/**
* @FullClassName com.rocket.myshop.dto.validattion.FirstGroup
* @Description: 用于beanvalidation的分组校验，优先级：1.FirstValid  2.SecondValid.class
* @author  Liu Jie
* @date 2016年12月12日 下午10:17:22 
* @version V1.0.0
 */
@GroupSequence( { FirstValid.class, SecondValid.class })  
public interface FirstValidGroup{

}
