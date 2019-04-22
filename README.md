# The magic of composition 

During this talk, we are going to dive into _Van Laarhoven_ __optics__ and 
unravel the mystery surrounding __lenses__. 

## Public

This talk is aimed for developers with basic notion of:
 - Scala
 - Functional programming
 
The examples are in written in Scala so a basic notion of the syntax is assumed. 

If you are not acquainted with the words `Functor, Appicative, Foldable, Traversable`
I would recommend visiting first the [typeclassopedia](https://wiki.haskell.org/Typeclassopedia) 
and get a basic notion of these concepts.   

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

Lenses provides __more composable__ versions of the abstractions you already known how to use in Haskell/Scala.

 - [lenses](https://github.com/ekmett/lens#lens-lenses-folds-and-traversals) (haskell)
 - [monocle](https://github.com/julien-truffaut/Monocle) (scala)
 
Both libraries provides rich APIs to work with lenses and a battery of utilities to make your life easier.

From my point of view, it's easier to start with [monocle](https://github.com/julien-truffaut/Monocle)
and follow with [lenses](https://github.com/ekmett/lens#lens-lenses-folds-and-traversals). 
The implementation of monocle is way more simple (and limited) than E. Kmett library.

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

##Credits: 
 - https://github.com/julien-truffaut/Monocle (J. Truffaut et al)
 - https://hackage.haskell.org/package/lens (E. Kmett)
