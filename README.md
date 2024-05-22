# 指令
## `/ingametips custom`
为玩家添加一个自定义的tip  
这个tip会保存至`config/ingametips/tips`目录下，文件名为`<id>.json`
### 语法
`/ingametips custom <targets> <id> <visible_time> <history> <title> <content>`
### 参数
- `<targets>`: 指定弹窗显示的玩家: 玩家目标选择器
- `<id>`: 弹窗的唯一标识符，用于指定客户端缓存的文件名: [word](https://zh.minecraft.wiki/w/%E5%8F%82%E6%95%B0%E7%B1%BB%E5%9E%8B#brigadier:string)
- `<visible_time>`: 弹窗显示时间，单位ms: int
- `<history>`: 是否加入历史tips列表: bool
- `<title>`: 弹窗标题: [phrase](https://zh.minecraft.wiki/w/%E5%8F%82%E6%95%B0%E7%B1%BB%E5%9E%8B#brigadier:string)
- `<content>`: 弹窗内容: [component](https://zh.minecraft.wiki/w/%E5%8F%82%E6%95%B0%E7%B1%BB%E5%9E%8B#component)

## `/ingametips add`
为玩家添加一个指定命名空间id的预设tip  
### 语法
`/ingametips add <targets> <id>`
### 参数
- `<targets>`: 指定弹窗显示的玩家: 玩家目标选择器  
- `<id>`: 预设tip的命名空间id  

# 预设配置
可以通过资源包添加预设的tips
## 配置文件位置
`<根目录>/assets/<namespace>/tips/<id>.json`  
然后，可以通过`/ingametips add`指令调用这个预设tip  

## 配置格式参考
```json
{
  "tab": "ingametips:default",
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
  "unlockText": {
    "translate": "tip.gui.locked"
  },
  "unlockHint": {
    "translate": "tip.gui.locked.content"
  },
  "visibleTime": 1000,
  "fontColor": "0xffc6fcff",
  "bgColor": "0xff000000"
}
```
## 字段说明
### tab
字符串，所属的标签，用于分类显示(暂未实现)

### contents  
> 参见 [Minecraft Wiki: 原始JSON文本](https://zh.minecraft.wiki/w/Tutorial:%E5%8E%9F%E5%A7%8BJSON%E6%96%87%E6%9C%AC)

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

### unlockText
Component，解锁前的条目标题文本

### unlockHint
Component，解锁前的条目内容文本