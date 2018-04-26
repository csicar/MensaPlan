#!/bin/runhaskell

import Control.Applicative
import Control.Monad
import System.FilePath
import System.Directory
import System.Process

getQualifiedDirectoryContents :: FilePath -> IO [FilePath]
getQualifiedDirectoryContents fp =
    map (fp </>) . filter (`notElem` [".",".."]) <$> getDirectoryContents fp

main = do
	files <- getQualifiedDirectoryContents "screenshots"
	mapM_ convert files
	print "ad"

convert file = do
	let smallFile = (dropExtension file)++".small.png"
	let cmd = "convert "++file++" -resize 300 "++smallFile
	print cmd
	smallFileExists <- doesFileExist smallFile
	when smallFileExists $ removeFile smallFile
	runCommand cmd 
