/**
 * 邀请机制
 * 注册时 inviteCode 作为邀请码 是最高判断条件
 * 任意链接(Filter级别) 只要进来并且发现 _inviteBy=id 就会在session中被记录
 *
 * @author CJ
 */
package me.jiangcai.dating.web;