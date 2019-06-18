# The magic of composition

During this talk, we are going to dive into _Monocle_ and
unravel the mystery surrounding __optics__.

Slides [here](https://monadplus.github.io/scala-lenses/).

## Public

This talk is aimed for developers with basic notion of:
 - Scala
 - Functional programming

The examples are in written in Scala so a basic notion of the syntax is assumed.

If you are not acquainted with the words `Functor, Appicative, Foldable, Traverse`
I would recommend visiting first the [cats](https://typelevel.org/cats/typeclasses.html)
and [typeclassopedia](https://wiki.haskell.org/Typeclassopedia) and get a basic notion of these concepts.

## Motivation

```scala
case class Street(number: Int, name: String)
case class Address(city: String, street: Street)
case class Company(name: String, address: Address)
case class Employee(name: String, company: Company)

val employee = Employee("john", Company("awesome inc", Address("london", Street(23, "high street"))))
```

Old plain scala:
```scala
employee.copy(
  company = employee.company.copy(
    address = employee.company.address.copy(
      street = employee.company.address.street.copy(
        name = employee.company.address.street.name.capitalize // luckily capitalize exists
      )
    )
  )
)
```

With lenses:
```scala
(company composeLens
 address composeLens
 street composeLens streetName).modify(_.capitalize)(employee)
```

## Libraries

Lenses provides __more composable__ versions of the abstractions you already known how to use in Scala/Haskell.

 - [monocle](https://github.com/julien-truffaut/Monocle) (scala)
 - [lenses](https://github.com/ekmett/lens#lens-lenses-folds-and-traversals) (haskell)

Both libraries provides rich APIs to work with lenses and a battery of utilities to make your life easier.
Lenses is based on Van Laarhoven optics while Monocle uses an alternative encoding. The reason behind not using
Van Laarhoven nor profunctor optics is because they are inefficient in scala and not well supported by the language
(scala does not support N-rank types).


## Hierarchy

![Simplified Class Diagram](https://raw.github.com/julien-truffaut/Monocle/master/image/class-diagram.png)

_Monocle lenses hierarchy_

![Class Diagram](http://i.imgur.com/ALlbPRa.png)

_Lenses hierarchy in haskell_

## Composition

The result is their lowest upper bound in the hierarchy (or an error if that bound doesn't exist).

|               | Getter     | Iso        | Lens       | Optional     | Prism      | Setter     | Traversal     |
| ------------- |:----------:|:----------:|:----------:|:------------:|:----------:|:----------:|:-------------:|
| **Getter**    | **Getter** | Getter     | Getter     | Fold         | Fold       | -          | Fold          |
| **Iso**       | Getter     | **Iso**    | Lens       | Optional     | Prism      | Setter     | Traversal     |
| **Lens**      | Getter     | Lens       | **Lens**   | Optional     | Optional   | Setter     | Traversal     |
| **Optional**  | Fold       | Optional   | Optional   | **Optional** | Optional   | Setter     | Traversal     |
| **Prism**     | Fold       | Prism      | Optional   | Optional     | **Prism**  | Setter     | Traversal     |
| **Setter**    | -          | Setter     | Setter     | Setter       | Setter     | **Setter** | Setter        |
| **Traversal** | Fold       | Traversal  | Traversal  | Traversal    | Traversal  | Setter     | **Traversal** |

## Credits:
 - https://github.com/julien-truffaut/Monocle (J. Truffaut et al)
 - https://hackage.haskell.org/package/lens (E. Kmett)
