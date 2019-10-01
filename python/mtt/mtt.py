from __future__ import print_function, division, absolute_import

import os, tempfile
import numbers
import numpy as np
from numpy import array, ones, zeros, vstack, concatenate, resize, isnan, nan

from collections import OrderedDict

import py4j
from py4j.java_gateway import JavaGateway
from py4j.java_collections import ListConverter, MapConverter
gateway = JavaGateway()

block_quantities = [
  'ratC',
  'Ctot',
  'Cprod',
  'Cdeg',
  'fw_tot',
  'rv_tot',
  'rate_fw',
  'rate_rv',
  'Afree',
  'Bfree',
  'Dfree',
  ]

def exactly_one(*args):
    n = 0
    for arg in args:
        if arg is not None:
            n += 1
    return True if n == 1 else False

def binReverse(n, field_size):
    b = bin(n)[-1:1:-1]
    return int(b + (field_size - len(b))*'0', 2)

def SRAMRuleToBinary(group, block, variable):
        return (variable & 0x1F) | ((block & 0x7) << 5) | ((group & 0x3) << 8)

def binaryToRuleCfg(b):
    return (
      ((b & (0x3 << 8)) >> 8), # group
      ((b & (0x7 << 5)) >> 5), # block
        b & 0x1F) # variable

def addSRAMRule(in_matrix, group, block, variable, wire):
    return vstack((in_matrix, array((SRAMRuleToBinary(group, block, variable),wire),dtype=in_matrix.dtype)))

def removeSRAMRule(in_matrix, group, block, variable):
    from numpy import array_equal
    rule = array((SRAMRuleToBinary(group, block, variable)),dtype=in_matrix.dtype)
    # for row in in_matrix:
    #     print('{} vs {}: {}'.format(row[0],rule,array_equal(row[0],rule)))
    #     if array_equal(row[0],rule):
    #         print('  wire: {}'.format(row[1]))
    return vstack((row for row in in_matrix if not array_equal(row[0],rule)))

def binrep(in_matrix):
    #return array([(bin(row[0])[2:].zfill(10),str(row[1])) if row[0]>0 else ('nan','') for row in in_matrix]).reshape((-1,2))
    return array([(bin(row[0])[2:].zfill(10),str(row[1])) for row in in_matrix])

class Wiring(object):
    ''' Wiring wrapper. '''
    class WiringBlocks(object):
        def __init__(self, wiring):
            self.wiring = wiring

        class WiringBlock(object):
            class WiringTerminal:
                def __init__(self, terminal):
                    self.terminal = terminal

                def __str__(self):
                    return self.terminal.toString()

                def __repr__(self):
                    return self.__str__()

                def setInput(self, label, value):
                    self.terminal.setInputFromAmount(label, value)

                def connect(self, dest_terminal, positive=True):
                    '''
                    OBSOLETE
                    '''
                    self.terminal.makeConnection(dest_terminal.terminal, positive)

                def setValue(self, value):
                    '''
                    Set current value (only for input terminals).
                    '''
                    self.terminal.setValue(value)

                def getValue(self):
                    '''
                    Get current value (only for input terminals).
                    '''
                    return self.terminal.getValue()

                def setObservable(self, name):
                    '''
                    Attaches an observable to this terminal.
                    '''
                    self.terminal.setObservable(name)

            def __init__(self, block):
                self.block = block

            def __str__(self):
                return self.block.toString()

            def __repr__(self):
                return self.__str__()

            # Terminals ******************************************

            @property
            def Atot(self):
                return self.WiringTerminal(self.block.Atot())
            @Atot.setter
            def Atot(self,value):
                if isinstance(value, numbers.Number):
                    self.Atot.setValue(value)
                else:
                    raise RuntimeError('Invalid value for Atot')

            @property
            def Afree(self):
                return self.WiringTerminal(self.block.Afree())

            @property
            def Btot(self):
                return self.WiringTerminal(self.block.Btot())
            @Btot.setter
            def Btot(self,value):
                if isinstance(value, numbers.Number):
                    self.Btot.setValue(value)
                else:
                    raise RuntimeError('Invalid value for Btot')

            @property
            def Bfree(self):
                return self.WiringTerminal(self.block.Bfree())

            @property
            def Ctot(self):
                return self.WiringTerminal(self.block.Ctot())

            @property
            def Cfree(self):
                return self.WiringTerminal(self.block.Cfree())

            @property
            def Cfree_cp(self):
                return self.WiringTerminal(self.block.Cfree_cp())

            @property
            def Cdeg(self):
                return self.WiringTerminal(self.block.Cdeg())

            @property
            def Cprod(self):
                return self.WiringTerminal(self.block.Cprod())

            @property
            def Ctot_in(self):
                return self.WiringTerminal(self.block.Ctot_in())

            @property
            def Dfree(self):
                return self.WiringTerminal(self.block.Dfree())
            @Dfree.setter
            def Dfree(self,value):
                if isinstance(value, numbers.Number):
                    self.Dfree.setValue(value)
                else:
                    raise RuntimeError('Invalid value for Dfree')

            @property
            def fw_up(self):
                return self.WiringTerminal(self.block.fw_up())

            @property
            def rv_up(self):
                return self.WiringTerminal(self.block.rv_up())

            @property
            def fw_tot(self):
                return self.WiringTerminal(self.block.fw_tot())

            @property
            def rv_tot(self):
                return self.WiringTerminal(self.block.rv_tot())

            @property
            def rate_fw(self):
                return self.WiringTerminal(self.block.rate_fw())

            @property
            def rate_rv(self):
                return self.WiringTerminal(self.block.rate_rv())

            # Parameters ******************************************

            @property
            def KDfw(self):
                return self.block.KDfw()
            @KDfw.setter
            def KDfw(self,v):
                self.block.set_KDfw(v)

            @property
            def KDrv(self):
                return self.block.KDrv()
            @KDrv.setter
            def KDrv(self,v):
                self.block.set_KDrv(v)

            @property
            def kr(self):
                return self.block.kr()
            @kr.setter
            def kr(self,v):
                self.block.set_kr(v)

            @property
            def kr(self):
                return self.block.kr()
            @kr.setter
            def kr(self,v):
                self.block.set_kr(v)

            @property
            def kdeg(self):
                return self.block.kdeg()
            @kdeg.setter
            def kdeg(self,v):
                self.block.set_kdeg(v)

            @property
            def ratC(self):
                return self.block.ratC()
            @ratC.setter
            def ratC(self,v):
                self.block.set_ratC(v)

            @property
            def n(self):
                return self.block.n()
            @n.setter
            def n(self,v):
                self.block.set_n(float(v))

            @property
            def A_FB_EN(self):
                return self.block.A_FB_EN()
            @A_FB_EN.setter
            def A_FB_EN(self,v):
                self.block.set_A_FB_EN(v)

            @property
            def B_FB_EN(self):
                return self.block.B_FB_EN()
            @B_FB_EN.setter
            def B_FB_EN(self,v):
                self.block.set_B_FB_EN(v)

            def printCurrentConfig(self):
                return self.block.printCurrentConfig()

            def getEffective_kf(self):
                if (self.Btot.getValue() > 0.):
                    if (self.Atot.getValue() > 0.):
                        return self.kr*self.Atot.getValue()*self.Btot.getValue()/self.KDfw
                    else:
                        return self.kr*self.Btot.getValue()/self.KDfw
                else:
                    return self.kr/self.KDfw

            def getDigitizedParameterString(self, mtt):
                '''
                Get a string of the digitized parameter values.
                '''
                    # '  Atot = {}\n'.format(self.Atot.getValue())
                return str(self)+'\n'+\
                    '  Atot = {} ({}, {})\n'.format(self.Atot.getValue(), *[hex(v) for v in mtt.digitize(self.Atot.getValue())])+\
                    '  Btot = {} ({}, {})\n'.format(self.Btot.getValue(), *[hex(v) for v in mtt.digitize(self.Btot.getValue())])+\
                    '  KDfw = {} ({}, {})\n'.format(self.KDfw, *[hex(v) for v in mtt.digitize(self.KDfw)])+\
                    '  kr = {} ({}, {})\n'.format(self.kr, *[hex(v) for v in mtt.digitize(self.kr)])+\
                    '  kdeg = {} ({}, {})\n'.format(self.kdeg, *[hex(v) for v in mtt.digitize(self.kdeg)])+\
                    '  Dfree = {} ({}, {})\n'.format(self.Dfree.getValue(), *[hex(v) for v in mtt.digitize(self.Dfree.getValue())])+\
                    '  KDrv = {} ({}, {})\n'.format(self.KDrv, *[hex(v) for v in mtt.digitize(self.KDrv)])+\
                    '  ratC = {} ({}, {})\n'.format(self.ratC, *[hex(v) for v in mtt.digitize(self.ratC)])+\
                    '  n = {} ({}, {})\n'.format(self.n, *[hex(v) for v in mtt.digitize(self.n)])+\
                    '  switches:\n'+\
                    '  A_FB_EN = {}\n'.format(self.A_FB_EN)+\
                    '  B_FB_EN = {}\n'.format(self.B_FB_EN)+\
                    '  effective kf = {}\n'.format(self.getEffective_kf())


        def __len__(self):
            return self.wiring.getNumBlocks()

        def __str__(self):
            return '[{}]'.format(', '.join((str(block) for block in self)))

        def __repr__(self):
            return '[{}]'.format(', '.join(repr(block) for block in self))

        def __getitem__(self, index):
            return self.WiringBlock(self.wiring.getBlock(index))

        #def __setitem__(self, index, value):
            #pass

        def __iter__(self):
            return (self.WiringBlock(self.wiring.getBlock(index)) for index in range(len(self)))

    def connect(self, src_terminal, dest_terminal, invert=False):
        self.wiring.connect(src_terminal.terminal, dest_terminal.terminal, invert)

    def __init__(self, wiring):
        self.wiring = wiring
        self.blocks = self.WiringBlocks(self.wiring)

    def __str__(self):
        return self.wiring.toString()

    def __repr__(self):
        return self.wiring.toString()

    @classmethod
    def bare(cls, block_names):
        return cls(gateway.jvm.com.cytocomp.mtt.BareWiringJ.apply(
          ListConverter().convert(block_names, gateway._gateway_client)))

    @classmethod
    def fromFile(cls, sbml_file, margins={}, default_margin=10., input_val=1., current_assignment='universal', disable_block_elision=False, wiring_switch=False):
        if not wiring_switch:
            return cls(gateway.jvm.com.cytocomp.mtt.Wiring2FromFile.apply(sbml_file, MapConverter().convert(margins, gateway._gateway_client), default_margin, input_val, current_assignment, disable_block_elision))
        else:
            return cls(gateway.jvm.com.cytocomp.mtt.WiringFromFile.apply(sbml_file))


class SRAMProgram(object):
    def __init__(self, program):
        self.program = program

    def _convert(self, collection):
        if isinstance(collection, py4j.java_collections.JavaMap):
            return dict((k,self._convert(v)) for k,v in sorted(collection.items()))
        elif isinstance(collection, py4j.java_collections.JavaList):
            return list(self._convert(v) for v in collection)
        elif isinstance(collection, py4j.java_collections.JavaSet):
            return set(self._convert(v) for v in collection)
        else:
            return collection

    def dump(self):
        return self._convert(self.program.dump())

    def convertRule(self,rule):
        return [rule.getVariable(), rule.getBlock(), rule.getGroup(), rule.getWire()]

    def getRoutingRules(self):
        raw_rules = self.program.getRoutingRulesJ()
        return [self.convertRule(rule) for rule in raw_rules]

    def getRoutingRulesMatrix(self):
        return array(self.getRoutingRules())

    def ruleToBinary(self, rule):
        b = SRAMRuleToBinary(rule[2], rule[1], rule[0])
        return b

    def getRoutingRulesBinaryMatrix(self):
        '''
        Returns a binary matrix of the routing rules.
        Each row consists of 10 columns of zeros and ones.
        '''
        return array([(self.ruleToBinary(rule),rule[3]) for rule in self.getRoutingRules()])

    @classmethod
    def convertToSRAMMatrix(cls, in_matrix):
        result = ones((1024,100),dtype=int)
        for k,row in enumerate(in_matrix):
            if row[0] > 0: # FIXME: remove
                result[row[0],row[1]] = 0
        return result

    def getSRAMMatrix(self):
        '''
        Converts each routing rule to its decimal representation,
        then stores the rule in the corresponding row in the SRAM
        matrix. The SRAM matrix consists of 1024 rows (10-bit max int)
        and 100 columns (100 SRAM wires).
        '''
        return self.convertToSRAMMatrix(self.getRoutingRulesBinaryMatrix())

class ShiftRegProgram(object):
    def __init__(self, mtt, wiring, sram_program):
        self.mtt = mtt
        self.wiring = wiring
        self.sram_program = sram_program

    def toBinaryCurrent(self, base, value):
        return array([(value>>k)&1 for k in range(0,4)] +
          [(base>>k)&1 for k in range(0,3)], dtype='bool')

    def getProgrammingVector(self, use_Ctot=True):
        #result = zeros((286+4*9,4),dtype='bool') # FIXME: hardcoded
        result = zeros((350,4),dtype='bool') # FIXME: 315 now?
        for g,group in enumerate(self.sram_program.program.getChip(0).getGroupsListJ()):
            # g = group index
            if g >= 1:
                break # FIXME: remove
            for block_index in range(3): # FIXME: change to 4
                block = self.wiring.blocks[block_index+3*g] # FIXME: change to 4
                k = block_index*56 # block start
                # print('Atot = {}, digitized = {}'.format(block.Atot.getValue(), self.toBinaryCurrent(*self.mtt.digitize(block.Atot))))
                result[k+0 :k+7, g] = self.toBinaryCurrent(*self.mtt.digitize(block.Atot))
                result[k+7 :k+14,g] = self.toBinaryCurrent(*self.mtt.digitize(block.Btot))
                result[k+14:k+21,g] = self.toBinaryCurrent(*self.mtt.digitize(block.KDfw))
                result[k+21:k+28,g] = self.toBinaryCurrent(*self.mtt.digitize(block.kr))
                result[k+28:k+35,g] = self.toBinaryCurrent(*self.mtt.digitize(block.kdeg))
                result[k+35:k+42,g] = self.toBinaryCurrent(*self.mtt.digitize(block.Dfree))
                result[k+42:k+49,g] = self.toBinaryCurrent(*self.mtt.digitize(block.KDrv))
                result[k+49:k+56,g] = self.toBinaryCurrent(*self.mtt.digitize(block.ratC))

                # offset for switches
                k = 287+9*block_index

                # set switches
                result[k+0 :k+4, g] = array([1, 1, 1, 1],dtype='bool') if block.n == 1 else array([0, 1, 0, 1],dtype='bool') # FIXME
                result[k+4 :k+6, g] = array([block.A_FB_EN, block.B_FB_EN], dtype='bool')
                result[k+6 :k+9, g] = array([False, use_Ctot, True], dtype='bool') # FIXME: useI is FF_EN1-4
        return result

    def getProgrammingVectorIndexed(self, use_Ctot=True, block_indices=range(3)):
        result = zeros((350,4),dtype='bool')
        for n,block_index in enumerate(block_indices):
            block = self.wiring.blocks[n]
            g = int(block_index/5)
            b = block_index%5
            k = b*56 # block start
            # print('Atot = {}, digitized = {}'.format(block.Atot.getValue(), self.toBinaryCurrent(*self.mtt.digitize(block.Atot))))
            print(k,g)
            result[k+0 :k+7, g] = self.toBinaryCurrent(*self.mtt.digitize(block.Atot))
            result[k+7 :k+14,g] = self.toBinaryCurrent(*self.mtt.digitize(block.Btot))
            result[k+14:k+21,g] = self.toBinaryCurrent(*self.mtt.digitize(block.KDfw))
            result[k+21:k+28,g] = self.toBinaryCurrent(*self.mtt.digitize(block.kr))
            result[k+28:k+35,g] = self.toBinaryCurrent(*self.mtt.digitize(block.kdeg))
            result[k+35:k+42,g] = self.toBinaryCurrent(*self.mtt.digitize(block.Dfree))
            result[k+42:k+49,g] = self.toBinaryCurrent(*self.mtt.digitize(block.KDrv))
            result[k+49:k+56,g] = self.toBinaryCurrent(*self.mtt.digitize(block.ratC))

            # offset for switches
            k = 287+9*b

            # set switches
            result[k+0 :k+4, g] = array([1, 1, 1, 1],dtype='bool') if block.n == 1 else array([0, 1, 0, 1],dtype='bool') # FIXME
            result[k+4 :k+6, g] = array([block.A_FB_EN, block.B_FB_EN], dtype='bool')
            result[k+6 :k+9, g] = array([False, use_Ctot, True], dtype='bool') # FIXME: useI is FF_EN1-4
        return result

class MTT(object):
    class BlocksWrapper(object):
        def __init__(self, mtt):
            self.mtt = mtt

        def __len__(self):
            return self.mtt.getNumBlocks()

        class BlockWrapper(object):
            def __init__(self, mtt, index):
                self.mtt = mtt
                self.index = index

            def getName(self):
                return self.mtt.getBlockName(self.index)

            def getValue(self):
                return self.mtt.getBlockValue(self.index)

            def getRate(self):
                return self.mtt.getBlockRate(self.index)

            def get_Cprod(self):
                return self.mtt.get_Cprod(self.index)

            def get_Cdeg(self):
                return self.mtt.get_Cdeg(self.index)

            def get_rate_fw(self):
                return self.mtt.get_rate_fw(self.index)

            def get_rate_rv(self):
                return self.mtt.get_rate_rv(self.index)

            def get_Afree(self):
                return self.mtt.get_Afree(self.index)

            def get_Bfree(self):
                return self.mtt.get_Bfree(self.index)

            def get_Dfree(self):
                return self.mtt.get_Dfree(self.index)

            def getQuantity(self, quantity):
                block = self.mtt.getBlock(self.index)
                if quantity == 'ratC':
                    return block.ratC()
                elif quantity == 'Ctot':
                    return self.getValue()
                elif quantity == 'rate':
                    return self.getRate()
                elif quantity == 'Cprod':
                    return self.get_Cprod()
                elif quantity == 'Cdeg':
                    return self.get_Cdeg()
                elif quantity == 'rate_fw':
                    return self.get_rate_fw()
                elif quantity == 'rate_rv':
                    return self.get_rate_rv()
                elif quantity == 'fw_tot':
                    return self.get_fw_tot()
                elif quantity == 'rv_tot':
                    return self.get_rv_tot()
                elif quantity == 'Afree':
                    return self.get_Afree()
                elif quantity == 'Bfree':
                    return self.get_Bfree()
                elif quantity == 'Dfree':
                    return self.get_Dfree()
                else:
                    # return self.mtt.getQuantity(quantity)
                    raise KeyError('No such quantity "{}"'.format(quantity))

            def __str__(self):
                return '{}: {}'.format(self.getName(), self.getValue())

            def __repr__(self):
                return self.__str__()

            def __dir__(self):
                extra = ['rate']
                return super(BlockWrapper, self).__dir__ + extra + block_quantities

            def __getattr__(self, attr):
                if attr in block_quantities:
                    return self.getQuantity(attr)
                raise AttributeError('No such attribute "{}"'.format(attr))

            @property
            def rate(self):
                return self.mtt.getBlockRate(self.index)

            def getSummary(self):
                return self.mtt.getValueSummary(self.index)

        def __getitem__(self, index):
            return self.BlockWrapper(self.mtt, index)

        def __setitem__(self, index, value):
            self.mtt.setBlockValue(index, value)

        def __iter__(self):
            return (self.BlockWrapper(self.mtt, index) for index in range(len(self)))

        def __str__(self):
            return '[{}]'.format(', '.join((str(block) for block in self)))

        def __repr__(self):
            return '[{}]'.format(', '.join(repr(block) for block in self))

        def __getattr__(self, attr):
            if attr in block_quantities:
                return OrderedDict((block.getName(),block.getQuantity(attr)) for block in self)
            if attr == 'rate':
                return OrderedDict((block.getName(),block.rate) for block in self)
            raise AttributeError('No such attribute "{}"'.format(attr))

        def getSummary(self):
            return '\n\n'.join((block.getName()+':\n'+block.getSummary() for block in self))

        def getUpdatedSummary(self):
            """ Updates values before getting summary."""
            self.mtt.updateRates()
            return '\n\n'.join((block.getName()+':\n'+block.getSummary() for block in self))

    def __init__(self, sbml_file=None, wiring=None):
        """ model is an SBML file."""
        if not exactly_one(sbml_file, wiring):
            raise RuntimeError('Must specify either sbml_file or wiring, not both')
        if isinstance(sbml_file, Wiring):
            wiring = sbml_file
            sbml_file = None
        if sbml_file is not None:
            self.mtt = gateway.jvm.com.cytocomp.mtt.MTTFromFile.apply(sbml_file)
            #self.mtt.loadSBMLFile(sbml_file)
            self.stored_timecourse = None
            self.blocks = self.BlocksWrapper(self.mtt)
        elif wiring is not None:
            self.mtt = gateway.jvm.com.cytocomp.mtt.MTTFromWiring.apply(wiring.wiring)
            self.stored_timecourse = None
            self.blocks = self.BlocksWrapper(self.mtt)

    def reset(self):
        self.mtt.reset()

    def updateRates(self):
        self.mtt.updateRates()

    def simulate(self, start, stop, points, block_output=False):
        if not isinstance(start, numbers.Number):
            raise RuntimeError('Expected a number')
        if not isinstance(stop, numbers.Number):
            raise RuntimeError('Expected a number')
        if not isinstance(points, numbers.Number):
            raise RuntimeError('Expected a number')
        if points < 2:
            raise RuntimeError('Not enough points')

        simresults = self.mtt.simulate(float(start), float(stop), int(points)-1, block_output)

        #print('colnames: {}'.format(simresults.colnames))
        #print('getColNames: {}'.format(simresults.getColNames()))
        #for k in range(simresults.getNumColumns()):
            #print('column {}:\n{}'.format(k, simresults.getColumn(k)))

        nrows = int(points)
        # one extra for time col
        ncols = int(simresults.getNumColumns())+1
        columns = [simresults.getTimeColumn()]
        for j in range(ncols-1):
            columns.append(simresults.getColumn(j))
        result = NamedArray((nrows,ncols), colnames=['time']+list(simresults.getColNames()))
        for i in range(nrows):
            for j in range(ncols):
                result[i,j] = columns[j][i]

        self.stored_timecourse = result
        return result

    def plot(self, result=None, show=True,
             xtitle=None, ytitle=None, title=None, xlim=None, ylim=None, logx=False, logy=False,
             xscale='linear', yscale='linear', grid=False, ordinates=None, tag=None, labels=None, **kwargs):
        """ Plot the results
        """
        import tellurium as te
        if result is None:
            result = self.stored_timecourse

        if ordinates:
            kwargs['ordinates'] = ordinates
        if title:
            kwargs['title'] = title
        if xtitle:
            kwargs['xtitle'] = xtitle
        if ytitle:
            kwargs['ytitle'] = ytitle
        if xlim:
            kwargs['xlim'] = xlim
        if ylim:
            kwargs['ylim'] = ylim
        if logx:
            kwargs['logx'] = logx
        if logy:
            kwargs['logy'] = logy
        if tag:
            kwargs['tag'] = tag

        engine = te.getPlottingEngine()
        if show:
            # show the plot immediately
            engine.plotTimecourse(result, **kwargs)
        else:
            # otherwise, accumulate the traces
            engine.accumulateTimecourse(result, **kwargs)

    def getWiringString(self):
        return self.mtt.getWiringString()

    def renderDiagram(self, columns, filepath):
        if not isinstance(columns, numbers.Number):
            raise RuntimeError('Expected a number')
        self.mtt.renderDiagram(int(columns), str(filepath))

    def draw(self, columns):
        """IPython wrapper for renderDiagram.
        Shows a diagram of the wiring in a Jupyter cell."""
        f,fname = tempfile.mkstemp(suffix='.png')
        self.renderDiagram(columns,fname)
        from IPython.display import Image
        img = Image(filename=fname)
        os.remove(fname)
        return img

    def compileSRAM(self):
        return SRAMProgram(self.mtt.compileSRAM())

    def compileSRAMWithBlockRemap(self, block_indices):
        return SRAMProgram(self.mtt.compileSRAMWithBlockRemap(ListConverter().convert(block_indices, gateway._gateway_client)))

    def compileShiftReg(self):
        return ShiftRegProgram(self, Wiring(self.mtt.getWiring()), SRAMProgram(self.mtt.compileSRAM()))

    def digitize(self,v):
        if not isinstance(v, numbers.Number):
            return list(self.mtt.digitize(v.getValue()))
        else:
            return list(self.mtt.digitize(v))

    def exportToSimulink(self, target_directory, system_name, n_columns):
        self.mtt.exportToSimulink(target_directory, system_name, n_columns)

    def toSBML(self):
        return self.mtt.toSBML()

    def getReactionBlockIndices(self):
        return self.mtt.getReactionBlockIndices()


# https://docs.scipy.org/doc/numpy-1.13.0/user/basics.subclassing.html
class NamedArray(np.ndarray):
    def __new__(subtype, shape, dtype=float, buffer=None, offset=0,
                strides=None, order=None, colnames=[]):
        obj = super(NamedArray, subtype).__new__(subtype, shape, dtype,
                                                buffer, offset, strides,
                                                order)
        obj.colnames = colnames
        return obj

    def __array_finalize__(self, obj):
        if obj is None: return
        self.colnames = getattr(obj, 'colnames', [])

    def __str__(self):
        longest_header = max((len(c) for c in self.colnames))
        col_len = max(longest_header, 10)
        header_row = np.array([('{:'+str(col_len)+'}').format(x) for x in self.colnames], dtype=object)
        #return np.array2string(header_row)
        s = np.array2string(vstack([header_row,self]), formatter={'str': lambda x: x})
        return s

    def rawColumnFor(self, id):
        k = self.colnames.index(id)
        return self[:,k]

    def columnFor(self, id):
        id = "'{}'".format(id)
        k = self.colnames.index(id)
        return self[:,k]

    def getLatestValues(self):
        v = [v for v in self.colnames if v != 'time']
        return {id.replace("'",''): self.rawColumnFor(id)[-1] for id in v}
