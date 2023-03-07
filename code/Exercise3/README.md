# Code of your exercise

On peut définir une nouvelle règle : 

```
<rule name="NestedIf"
      language="java"
      message="Too much nested if."
      class="net.sourceforge.pmd.lang.rule.XPathRule" >
    <description>
        TODO
    </description>
    <priority>3</priority>
    <properties>
        <property name="xpath">
            <value>
                <![CDATA[
//IfStatement[descendant::IfStatement[descendant::IfStatement]]
]]>
            </value>
        </property>
    </properties>
</rule>
```

On teste avec ce bout de code : 

```
if(true) {
    System.out.println("test");
    if(true) {
        if (true) {
            System.out.println("test");
        }
    }
}
```

![](https://codimd.math.cnrs.fr/uploads/upload_dc266e3c196fdb87256d1567f924be90.png)
