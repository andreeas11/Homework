
-- =============== DO NOT MODIFY ===================

{-# LANGUAGE ExistentialQuantification #-}
{-# LANGUAGE FlexibleInstances #-}

-- ==================================================

-- module Skel where
module Tasks where

import Dataset
import Data.List
import Text.Printf
import Data.Array

import Common

type CSV = String
type Value = String
type Row = [Value]
type Table = [Row]
-- type Query = Table
type ColumnName = String

-- Prerequisities
split_by :: Char -> String -> [String]
split_by x = foldr op [""]
  where op char acc
            | char == x = "":acc
            | otherwise = (char:head(acc)):tail(acc)

read_csv :: CSV -> Table
read_csv = (map (split_by ',')) . (split_by '\n')

write_csv :: Table -> CSV
write_csv = (foldr (++) []).
            (intersperse "\n").
            (map (foldr (++) [])).
            (map (intersperse ","))


{-
    TASK SET 1
-}


-- Task 1

sumaLista :: Row -> Row
sumaLista m = head m : [printf "%.2f" (foldr (+) 0 (map (read::String->Float) (tail m) :: [Float]) / 8)]

sumaListaDeLista :: Table -> Table
sumaListaDeLista m = map sumaLista (tail m)

compute_average_steps :: Table -> Table
compute_average_steps m = ["Name","Average Number of Steps"] : sumaListaDeLista m

-- Task 2

-- Number of people who have achieved their goal:
get_passed_people_num :: Table -> Int
get_passed_people_num m = foldr (\x acc-> if 8 * x > 1000 then 1 + acc else acc) 0 (map (read :: String->Float) (map last (tail( compute_average_steps m))))


-- Percentage of people who have achieved their:
get_passed_people_percentage :: Table -> Float
get_passed_people_percentage m = fromIntegral (get_passed_people_num m ) / fromIntegral (length m-1)


-- Average number of daily steps
get_steps_avg :: Table -> Float
get_steps_avg m = (foldr (+) 0 (map (read :: String->Float) (map last (tail( compute_average_steps m))))) * 8 / fromIntegral (length m-1)


-- Task 3
myavg :: [String ] -> String
myavg l = printf "%.2f" (foldr (+) 0 (map (read::String->Float) l :: [Float]) / fromIntegral (length l))

get_avg_steps_per_h :: Table -> Table
get_avg_steps_per_h m = ["H10","H11","H12","H13","H14","H15","H16","H17"] : [map (myavg) (tail (transpose (tail m)))]


-- Task 4

getNrOfActive1 :: Row -> Int
getNrOfActive1 l = foldr (\x acc -> if (x >= 0 && x < 50) then (acc + 1) else acc) 0 (map (read::String->Float) (tail l) :: [Float])

getNrOfActive2 :: Row -> Int
getNrOfActive2 l = foldr (\x acc -> if (x >= 50 && x < 100) then (acc + 1) else acc) 0 (map (read::String->Float) (tail l) :: [Float])

getNrOfActive3 :: Row -> Int
getNrOfActive3 l = foldr (\x acc -> if (x >= 100 && x < 500) then (acc + 1) else acc) 0 (map (read::String->Float) (tail l) :: [Float])

get_activ_summary :: Table -> Table
get_activ_summary m = ("column":"range1":"range2":["range3"]) :
    ("VeryActiveMinutes" : show (getNrOfActive1 (map (!! 3) m)) : show (getNrOfActive2 (map (!! 3) m)) : [show (getNrOfActive3 (map (!! 3) m))]) :
    ("FairlyActiveMinutes" : show (getNrOfActive1 (map (!! 4) m)) : show (getNrOfActive2 (map (!! 4) m)) : [show (getNrOfActive3 (map (!! 4) m))]) :
    ["LightlyActiveMinutes" : show (getNrOfActive1 (map (!! 5) m)) : show (getNrOfActive2 (map (!! 5) m)) : [show (getNrOfActive3 (map (!! 5) m))]]


-- Task 5

getTable :: Table -> Table
getTable m = transpose (map head (tail m) : [map (head.tail) (tail m)])

myCompare :: Row -> Row -> Int
myCompare a b
        | (read :: String->Float) (last a) == (read :: String->Float) (last b) = if head a < head b then -1 else 1
        | (read :: String->Float) (last a) < (read :: String->Float) (last b) = -1
        | otherwise = 1

myinsert :: Row -> Table -> Table
myinsert l [] = [l]
myinsert l (x:xs)
        | myCompare l x == -1 = l : (x:xs)
        | xs == [] = if myCompare l x < 1 then l : [x] else x : [l]
        | otherwise = x : myinsert l xs

insertSort :: Table -> Table
insertSort [] = []
insertSort (x:xs)
        | xs == [] = x:xs
        | otherwise = myinsert x (insertSort xs)

get_ranking :: Table -> Table
get_ranking m = ["Name","Total Steps"] : insertSort (getTable m)


-- Task 6

mysum :: [String] -> String
mysum l = printf "%.2f" (foldr (+) 0 (map (read::String->Float) (tail l)) / 4)

my2sum :: [String] -> String
my2sum l = printf "%.2f" (foldr (+) 0 (map (read::String->Float) (drop 5 l)) / 4)

my1sum :: [String] -> String
my1sum l = printf "%.2f" ((read::String->Float) (mysum l) - (read::String->Float) (my2sum l))

mydif :: [String] -> String
mydif l = printf "%.2f" (abs ((read::String->Float) (mysum l) - (2 * ((read::String->Float) (my1sum l)))))

get2Table :: Table -> Table
get2Table m = transpose (map head (tail m) : map my1sum (tail m) : map my2sum (tail m) : [map mydif (tail m)])

get_steps_diff_table :: Table -> Table
get_steps_diff_table m = ["Name","Average first 4h","Average last 4h","Difference"] : insertSort (get2Table m)


-- Task 7

-- Applies the given function to all the values
vmap :: (Value -> Value) -> Table -> Table
vmap f m = map (map f) m


-- Task 8

-- Applies the given function to all the entries
rmap :: (Row -> Row) -> [String] -> Table -> Table
rmap f s m = s : map f (tail m)

get_sleep_total :: Row -> Row
get_sleep_total r = head r : [printf "%.2f" (foldr (+) 0 (map (read::String->Float) (tail r) :: [Float]))]

{-
    TASK SET 2
-}

-- Task 1

getColumn :: ColumnName -> Int -> Row -> Int
getColumn column nr [] = nr
getColumn column nr (x:xs) = if column == x then nr + 1 else getColumn column (nr + 1) xs

myCompare2 :: ColumnName -> Row -> Row -> Row -> Int
myCompare2 column x a b
        | (read :: String->Float) (last (take (getColumn column 0 x) a)) == (read :: String->Float) (last (take (getColumn column 0 x) b)) = if head a < head b then -1 else 1
        | (read :: String->Float) (last (take (getColumn column 0 x) a)) < (read :: String->Float) (last (take (getColumn column 0 x) b)) = -1
        | otherwise = 1

myinsert2 :: ColumnName -> Row -> Row -> Table -> Table
myinsert2 column a l [] = [l]
myinsert2 column a l (x:xs)
        | myCompare2 column a l x == -1 = l : (x:xs)
        | xs == [] = if myCompare2 column a l x < 1 then l : [x] else x : [l]
        | otherwise = x : myinsert2 column a l xs

insertSort2 :: ColumnName -> Row -> Table -> Table
insertSort2 column a [] = []
insertSort2 column a (x:xs)
        | xs == [] = x:xs
        | otherwise = myinsert2 column a x (insertSort2 column a xs)

tsort :: ColumnName -> Table -> Table
tsort column table = head table : insertSort2 column (head table) (tail table)

-- Task 2

vunion :: Table -> Table -> Table
vunion t1 t2
        | head t1 == head t2 = t1 ++ (tail t2)
        | otherwise = t1

-- Task 3

constructRow nr l
        | nr > 1 = constructRow (nr - 1) l ++ [""]
        | otherwise = l

construct :: Table -> Int -> Int -> Table
construct table nr columns
        | nr > 0 = construct (table ++ [constructRow columns [""]]) (nr - 1) columns
        | otherwise = table

hunion :: Table -> Table -> Table
hunion t1 t2
        | length (head (transpose t1)) < length (head (transpose t2)) = zipWith (++) (construct t1 ((length (head (transpose t2))) - (length (head (transpose t1)))) (length (head t1))) t2
        | length (head (transpose t1)) > length (head (transpose t2)) = zipWith (++) t1 (construct t2 ((length (head (transpose t1))) - (length (head (transpose t2)))) (length (head t2)))
        | otherwise = zipWith (++) t1 t2

-- Task 4

joinAux t1 t2
        | t1 == [] || t2 == [] = []
        | elem (head (head t1)) (head (transpose t2)) = head t1 : joinAux (tail t1) (tail t2)
        | otherwise = head t2 : joinAux (tail t1) (tail t2)

joinAux1 t1 t2 
        | t1 == [] || t2 == [] = []
        | elem (head (head t2)) (head (transpose t1)) == False = head t2 : joinAux1 (tail t1) (tail t2)
        | otherwise =  joinAux1 (tail t1) (tail t2)

tjoin :: ColumnName -> Table -> Table -> Table
tjoin key_column t1 t2 = ((head t1) ++ (tail (head t2))) : (zipWith (++) (joinAux (tail t1) (tail t2)) (transpose (joinAux1 (transpose (joinAux (tail t1) (tail t2))) (transpose (tail t2)))))

-- Task 5

forEach :: (Row -> Row -> Row) -> Row -> Table -> Table
forEach new_row_function [] _ = []
forEach _ (_:_) [] = []
forEach new_row_function x (y:ys) = new_row_function x y : forEach new_row_function x ys

forEach2 :: (Row -> Row -> Row) -> Table -> Table -> Table
forEach2 new_row_function [] _ = []
forEach2 _ (_:_) [] = []
forEach2 new_row_function (x:xs) (y:ys) = forEach new_row_function x (y:ys) ++ forEach2 new_row_function xs (y:ys)

cartesian :: (Row -> Row -> Row) -> [ColumnName] -> Table -> Table -> Table
cartesian new_row_function new_column_names t1 t2 = new_column_names : forEach2 new_row_function (tail t1) (tail t2)

-- Task 6

getHeaders :: [ColumnName] -> Table -> Table
getHeaders [] t = []
getHeaders columns t = if head columns == head (head t) then head (transpose t) : getHeaders (tail columns) (transpose (tail (transpose t))) else getHeaders columns (transpose (tail (transpose t)))

projection :: [ColumnName] -> Table -> Table
projection columns_to_extract t = transpose (getHeaders columns_to_extract t)

-- Task 7

verify :: (Value -> Bool) -> ColumnName  -> Int-> Table -> Table
verify condition key_column nr t
        | (tail (tail t)) == [] = if condition (head(last(take nr (transpose (tail t))))) then [head (tail t)] else []
        | condition (head(last(take nr (transpose (tail t))))) = head (tail t) : verify condition key_column nr (tail t)
        | otherwise = verify condition key_column nr (tail t)

filterTable :: (Value -> Bool) -> ColumnName -> Table -> Table
filterTable condition key_column t = head t : verify condition key_column (getColumn key_column 0 (head t)) t

-- Task 8 TO_DO


{-
    TASK SET 3
-}


-- 3.1

data Query =
    FromTable Table
    | AsList String Query
    | Sort String Query
    | ValueMap (Value -> Value) Query
    | RowMap (Row -> Row) [String] Query
    | VUnion Query Query
    | HUnion Query Query
    | TableJoin String Query Query
    | Cartesian (Row -> Row -> Row) [String] Query Query
    | Projection [String] Query
    | forall a. FEval a => Filter (FilterCondition a) Query -- 3.4
    | Graph EdgeOp Query -- 3.5

instance Show QResult where
    show (List l) = show l
    show (Table t) = show t

class Eval a where
    eval :: a -> QResult

instance Eval Query where
    eval (FromTable table) = Table (table)
    eval (AsList colname (FromTable table)) = List (tail (last (take (getColumn colname 0 (head table)) (transpose table))))
    eval (Sort colname (FromTable table)) = Table (tsort colname table)
    eval (ValueMap op (FromTable table)) = Table (vmap op table)
    eval (RowMap op colnames (FromTable table)) = Table (rmap op colnames table)
    eval (VUnion (FromTable table1) (FromTable table2)) = Table (vunion table1 table2)
    eval (HUnion (FromTable table1) (FromTable table2)) = Table (hunion table1 table2)
    eval (TableJoin colname (FromTable table1) (FromTable table2)) = Table (tjoin colname table1 table2)
    eval (Cartesian op colnames (FromTable table1) (FromTable table2)) = Table (cartesian op colnames table1 table2)
    eval (Projection colnames (FromTable table)) = Table (projection colnames table)


-- 3.2 & 3.3

type FilterOp = Row -> Bool

data FilterCondition a =
    Eq String a |
    Lt String a |
    Gt String a |
    In String [a] |
    FNot (FilterCondition a) |
    FieldEq String String

class FEval a where
    feval :: [String] -> (FilterCondition a) -> FilterOp

