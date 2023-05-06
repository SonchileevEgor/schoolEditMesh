Хранение состояния сцены в Json-формате.
========================================

Текущее состояние сцены хранится в виде экземпляра класса `URState`.

Он содержит четыре поля:

`JsonArray _state` - список билдеров и их параметров (в виде массива Json)
`JsonArray _graphicState` - список тел и их параметров отображения (в виде массива Json)
`JsonArray _anchorState` - список якорей и их параметров отображения (в виде массива Json)
`String _description` - описание данного состояния (момента, в который это состояние было создано). Например, при удалении тела создается точка восстановления с описанием "Удаление тела".

Запись в файл и чтение из файла производится стандартными методами. При записи в файл создается Json-массив, состоящий из вышеперечисленных трех массивов; описание в файл не сохраняется.

За запись отвечает метод `URState.writeToFile(String filename)`, за чтение - конструктор `URState(String filename)`.

Подробнее о формате каждого из массивов.
`_state` (список билдеров с параметрами) - основное поле, два других массива при чтении из файла могут быть пусты.

Формат:

    [
	    {"id": "ID билдера", "type": "Название билдера", "title": "Имя создаваемого тела", "params": {Параметры билдера}},
	    {"id": "ID билдера", "type": "Название билдера", "title": "Имя создаваемого тела", "params": {Параметры билдера}},
	    ...
    ]

Название билдера - то, что возвращает функция `alias()` билдера - то имя, под которым билдер зареган в `AllBuildersManager`.
Имя создаваемого тела стоит особняком от параметров, потому что так "исторически сложилось" со времён, когда у тел не было ID, а были только имена. Переделки приведут к большим жертвам, поэтому имя передаётся и хранится отдельно.
Параметры билдера - ещё один Json-массив, генерируемый функцией `getJsonParams()` билдера -- список пар "ключ - значение".

Пример:
    
    [
	    {"id":"ivan","type":"Point","title":"A","params":{"x":0,"y":0,"z":0}},
	    {"id":"sharik","type":"Sphere","title":"sph","params":{"center":"PNTivan1","radius":0.5}},
	    {"id":"rita","type":"Point","title":"B","params":{"x":0.25,"y":-0.75,"z":1}},
	    {"id":"maks","type":"LineTwoPoints","title":"AB","params":{"A":"PNTivan1","B":"PNTrita1"}},
	    {"id":"cowboy","type":"DefaultPlane","title":"pl","params":{}},
	    {"id":"palka","type":"PointOnPlaneProjection","title":"C","params":{"point":"PNTrita1","plane":"cowboy"}}
    ]
    
Здесь создается точка *"ivan"* по координатам, по ней строится сфера *"sharik"*, потом по координатам строится точка *"rita"*, через точки *"ivan"* и *"rita"* проводится прямая *"maks"*.
Добавляется плоскость Oxy *"cowboy"* (у билдера нет параметров), опускается высота *"palka"* из точки *"rita"*. Как это выгдядит на экране, см. в приложении "example1".

Почему *"PNTivan1"* вместо просто *"ivan"*? Потому что билдер принимает в качестве параметра **ID якоря**, а не тела.

    Когда мы создаем тело, на сцене могут появляться новые якоря. Они получают ID по следующей схеме:

    "<тип якоря><ID тела><порядковый номер якоря>"

    Тип якоря - PNT, RIB, POLY, CIRC (для точек, ребер, граней и кругов соответственно).
    Порядковый номер в следующем смысле: новые якоря добавляются при создании тела в некотором (фиксированном) порядке.

    Поэтому "PNTivan1" - первый якорь-точка, добавленный при создании тела "ivan".

`_graphicState` и `_anchorState` похожи по своему формату, первый используется для хранения параметров тел, а второй - для параметров якорей.

Формат следующий:

    [
	    {"id": "ID тела / якоря", "params": {Параметры отображения}},
	    {"id": "ID тела / якоря", "params": {Параметры отображения}},
	    ...
    ] // для тел
` `

    [
	    {"id": "ID тела / якоря", "title": "Имя якоря", "params": {Параметры отображения}},
	    {"id": "ID тела / якоря", "title": "Имя якоря", "params": {Параметры отображения}},
	    ...
    ] // для якорей

За хранение параметров отображения якорей в программе отвечает класс `AnchorManager`; для тел - `BodyStateManager`. Они и генерируют эти два Json-массива.

Для каждого типа тела и якоря есть список типов параметров. Для тел он инициализируется в конструкторе `BodyType`. Для якорей - в классе `AnchorState` и его производных: `PointAnchorState`, `RibAnchorState`, `PolyAnchorState` и `DiskAnchorState`. Сначала все параметры тела/якоря из этого списка инициализируются по умолчанию (для каждого типа параметра установлено значение по умолчанию), потом обновляются соответственно тому, что указано в "params".
Таким образом, если каких-то параметров отображения в Json-массиве не хватает, они будут инициализированы по умолчанию.

Итоговый файл будет выглядеть (в развернутом виде) примерно так:

    [
	    [
		    {"id":"ivan","type":"Point","title":"A","params":{"x":0,"y":0,"z":0}},
		    {"id":"sharik","type":"Sphere","title":"sph","params":{"center":"PNTivan1","radius":0.5}},
		    {"id":"rita","type":"Point","title":"B","params":{"x":0.25,"y":-0.75,"z":1}},
		    {"id":"maks","type":"LineTwoPoints","title":"AB","params":{"A":"PNTivan1","B":"PNTrita1"}},
		    {"id":"cowboy","type":"DefaultPlane","title":"pl","params":{}},
		    {"id":"palka","type":"PointOnPlaneProjection","title":"C","params":{"point":"PNTrita1","plane":"cowboy"}}
    	],
	    [
		    {"id":"sharik","params":{"facet_visible":true,"carcass_thickness":1,"visible":true,"carcass_color":{"R":0,"G":0,"B":0},"chosen":true,"exists":true,"facet_color":{"R":0.6,"G":0.8,"B":0.9}}},
		    {"id":"CLONERIBmaks1","params":{"chosen":false,"exists":true}},
		    {"id":"cowboy","params":{"visible":true,"chosen":false,"exists":true,"plane_indent":1,"facet_color":{"R":0.6,"G":0.8,"B":0.9}}},
		    {"id":"ivan","params":{"chosen":false,"exists":true}},
		    {"id":"maks","params":{"carcass_thickness":1,"visible":true,"carcass_color":{"R":0,"G":0,"B":0},"chosen":false,"exists":true}},
		    {"id":"rita","params":{"chosen":false,"exists":true}},
		    {"id":"CLONEPNTpalka1","params":{"chosen":false,"exists":true}},
		    {"id":"palka","params":{"chosen":false,"exists":true}}
	    ],
	    [
		    {"id":"PNTivan1","title":"A","params":{"carcass_thickness":3,"movable":true,"visible":true,"title_visible":true,"chosen":true,"facet_color":{"R":0,"G":0,"B":0}}},
		    {"id":"RIBpalka1","title":"BC.rib","params":{"carcass_thickness":1,"visible":true,"carcass_color":{"R":0,"G":0,"B":0},"line_mark":"none","label":"","chosen":false,"carcass_style":"plain"}},
		    {"id":"PNTrita1","title":"B","params":{"carcass_thickness":3,"movable":true,"visible":true,"title_visible":true,"chosen":false,"facet_color":{"R":0,"G":0,"B":0}}},
		    {"id":"RIBmaks1","title":"AB.rib","params":{"carcass_thickness":1,"visible":true,"carcass_color":{"R":0,"G":0,"B":0},"line_mark":"none","label":"","chosen":false,"carcass_style":"plain"}},
		    {"id":"PNTpalka1","title":"C","params":{"carcass_thickness":3,"movable":false,"visible":true,"title_visible":true,"chosen":false,"facet_color":{"R":0,"G":0,"B":0}}}
	    ]
    ]
    
Подробности реализации можно почитать в классах `BuilderParam`, `BuilderParamType`, `BodyType`, `EntityState` и производных, `AnchorManager`, `BodyStateManager`, `URState` и т.д.

Сцена, описанная в тексте: 

![](http://i.imgur.com/3cWOkkE.png)