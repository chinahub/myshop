package com.rocket.myshop.dto.validattion;

import javax.validation.GroupSequence;

/**
* @FullClassName com.rocket.myshop.dto.validattion.SecondValidGroup
* @Description: 用于beanvalidation的分组校验，优先级：1.SecondValid  2.FirstValid
* @author  Liu Jie
* @date 2016年12月12日 下午10:19:48 
* @version V1.0.0
 */
@GroupSequence( { SecondValid.class, FirstValid.class })  
public interface SecondValidGroup {

}
