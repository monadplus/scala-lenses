## Composition

```haskell
(.) :: (b -> c) -> (a -> b) -> a -> c
(.) g f a = g(f(a))
```
## The power of (.)
```haskell
(.)         :: (a -> b) -> (c -> a)           -> c           -> b
(.).(.)     :: (a -> b) -> (c -> d -> a)      -> c -> d      -> b
(.).(.).(.) :: (a -> b) -> (c -> d -> e -> a) -> c -> d -> e -> b
```

Functor do compose !  
```haskell
fmap           :: Functor f
               => (a -> b) -> f a         -> f b
fmap.fmap      :: (Functor f, Functor g)
               => (a -> b) -> f (g a)     -> f (g b)
fmap.fmap.fmap :: (Functor f, Functor g, Functor h)
               => (a -> b) -> f (g (h a)) -> f (g (h b))
```
Also, Foldable !
```haskell
foldMap                 :: (Foldable f, Monoid m)
                        => (a -> m) -> f a         -> m
foldMap.foldMap         :: (Foldable f, Foldable g, Monoid m)
                        => (a -> m) -> f (g a)     -> m
foldMap.foldMap.foldMap :: (Foldable f, Foldable g, Foldable h, Monoid m)
                        => (a -> m) -> f (g (h a)) -> m
```

And, Traverse !
```haskell
traverse                   :: (Traversable f, Applicative m)
                           => (a -> m b) -> f a         -> m (f b)
traverse.traverse          :: (Traversable f, Traversable g, Applicative m)
                           => (a -> m b) -> f (g a)     -> m (f (g b))
traverse.traverse.traverse :: (Traversable f, Traversable g, Traversable h, Applicative m)
                           => (a -> m b) -> f (g (h a)) -> m (f (g (h b)))
```

### Credit: 
 - https://github.com/ekmett/lens/wiki/Derivation