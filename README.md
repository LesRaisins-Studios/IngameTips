## 配置格式参考
```json
{
  "contents": [
    {
      "text": "test"
    },
    {
      "color": {
        "value": 16733525,
        "name": "red"
      },
      "text": "rua"
    }
  ],
  "alwaysVisible": false,
  "onceOnly": false,
  "hide": false,
  "visibleTime": 20000,
  "fontColor": "0xffc6fcff",
  "bgColor": "0xff000000"
}
```
## 字段说明
### contents  
> 格式参见[mcwiki: 原始JSON文本](https://zh.minecraft.wiki/w/Tutorial:%E5%8E%9F%E5%A7%8BJSON%E6%96%87%E6%9C%AC)

数组，包含若干个Component对象，每个Component对象代表一个段落  
其中第一个Component对象将被作为标题显示

### alwaysVisible
布尔值，hud弹窗是否永不自动消失  

### onceOnly
布尔值，是否仅在第一次解锁时弹窗

### hide
布尔值，是否在tips列表里隐藏

### visibleTime
整数，弹窗显示时间，单位毫秒

### fontColor
字符串，字体颜色，格式为0xAARRGGBB

### bgColor
字符串，背景颜色，格式为0xAARRGGBB

